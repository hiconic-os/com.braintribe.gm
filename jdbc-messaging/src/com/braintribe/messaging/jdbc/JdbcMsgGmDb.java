package com.braintribe.messaging.jdbc;

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import com.braintribe.exception.Exceptions;
import com.braintribe.execution.CountingThreadFactory;
import com.braintribe.execution.ExtendedThreadPoolExecutor;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmDb;
import com.braintribe.gm.jdbc.api.GmIndex;
import com.braintribe.gm.jdbc.api.GmRow;
import com.braintribe.gm.jdbc.api.GmTable;
import com.braintribe.logging.Logger;
import com.braintribe.messaging.jdbc.JdbcMsgGmDb.JdbcMsgListener.MessageNotificationDispatcher;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Queue;
import com.braintribe.model.messaging.Topic;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.utils.lcd.StringTools;
import com.braintribe.utils.stream.api.StreamPipes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author peter.gazdik
 */
public class JdbcMsgGmDb {

	private final DataSource dataSource;

	private final GmDb gmDb;

	public final MsgTable tableTopic;
	public final MsgTable tableQueue;

	private final String identifier;
	private final String sqlPrefix;

	private static final Logger log = Logger.getLogger(JdbcMsgGmDb.class);

	private final JdbcMsgListener newMsgListener;

	private final MessagingContext msgContext;
	private final JdbcMsgConnection msgConnection;

	private final ExtendedThreadPoolExecutor executor;

	private final AtomicInteger notificationCounter = new AtomicInteger();

	public JdbcMsgGmDb(DataSource _dataSource, String _sqlPrefix, MessagingContext _msgContext, JdbcMsgConnection _msgConnection) {
		verifyPostgres(_dataSource);

		dataSource = _dataSource;
		msgContext = _msgContext;
		msgConnection = _msgConnection;

		executor = new ExtendedThreadPoolExecutor(//
				0, Integer.MAX_VALUE, // pool size
				1L, TimeUnit.MINUTES, // keep alive
				new SynchronousQueue<>(), new CountingThreadFactory("jdbc-msg-threadpool") //
		);

		gmDb = GmDb.newDb(dataSource) //
				.withStreamPipeFactory(StreamPipes.simpleFactory()) //
				.withExecutor(executor).done();

		identifier = _sqlPrefix; // typically hc
		sqlPrefix = _sqlPrefix + "_msg_"; // typically hc_msg_

		tableTopic = new MsgTable(true);
		tableQueue = new MsgTable(false);

		newMsgListener = new JdbcMsgListener();
	}

	private static void verifyPostgres(DataSource dataSource) {
		try (Connection conn = dataSource.getConnection()) {
			DatabaseMetaData md = conn.getMetaData();
			String databaseProductName = md.getDatabaseProductName();
			if (!databaseProductName.toLowerCase().contains("postgre"))
				throw new IllegalStateException(
						"Cannot initialize JDBC messaging. Data source doesn't point to a PostgreSQL database, but: " + databaseProductName);

			try {
				conn.unwrap(org.postgresql.PGConnection.class);
			} catch (Exception e) {
				throw new IllegalStateException("Cannot initialize JDBC messaging. "
						+ "Data source seems to be PostgreSQL, but obtaining PGConnection failed. Product name: " + databaseProductName, e);
			}

		} catch (SQLException e) {
			throw Exceptions.unchecked(e, "Error while verifying database connection");
		}

	}

	/** @see JdbcConnectionProvider#deleteExpiredMessages() */
	public int deleteExpiredMessages() {
		int tDeleted = tableTopic.deleteExpiredMessages();
		int qDeleted = tableQueue.deleteExpiredMessages();

		return tDeleted + qDeleted;
	}

	private class MsgTable {

		private static final int SHORT_BODY_LIMIT = 1000;

		// we have two different columns for encoded message body, short and long
		// short one - up to SHORT_BODY_LIMIT, is passed as part of the notification (which has a limit of 8K bytes)
		// long one has to be queried extra
		public final GmColumn<Long> colIdLong = gmDb.autoIncrementPrimaryKeyCol("id");
		public final GmColumn<Date> colCreated = gmDb.date("created").done();
		public final GmColumn<String> colBodyShort = gmDb.string("bodyShort").done();
		public final GmColumn<String> colBodyLong = gmDb.string("bodyLong").done();
		public final GmColumn<String> colDstName = gmDb.shortString255("dstName").done();
		public final GmColumn<String> colAddresseeNodeId = gmDb.shortString255("nodeId").done();
		public final GmColumn<String> colAddresseeAppId = gmDb.shortString255("appId").done();
		public final GmColumn<Long> colExpiration = gmDb.longCol("expiration").done(); // null is treated as never expired

		public final GmTable table;

		public MsgTable(boolean isTopic) {
			String topicOrQueue = isTopic ? "topic" : "queue";

			GmIndex idxExpir = gmDb.index(sqlPrefix + "idx_expir_" + topicOrQueue, colExpiration);

			table = gmDb.newTable(sqlPrefix + topicOrQueue) //
					.withColumns(colIdLong, colCreated, colBodyShort, colBodyLong, colDstName, colAddresseeNodeId, colAddresseeAppId, colExpiration) //
					.withIndices(idxExpir) //
					.done();
		}

		public void sendMessage(JdbcMessageEnvelope envelope, Destination destination) {
			String bodyShort = null;
			String bodyLong = null;

			if (envelope.body.length() <= SHORT_BODY_LIMIT)
				bodyShort = envelope.body;
			else
				bodyLong = envelope.body;

			try {
				table.insert( //
						colCreated, new Date(), colBodyShort, bodyShort, //
						colBodyLong, bodyLong, //
						colDstName, destination.getName(), //
						colAddresseeNodeId, envelope.addresseeNodeId, //
						colAddresseeAppId, envelope.addresseeAppId, //
						colExpiration, envelope.expiration //
				);

			} catch (RuntimeException e) {
				throw Exceptions.contextualize(e, "Error while sending message to " + destination);
			}
		}

		public boolean deleteMessage(Long id) {
			return table.delete().whereColumn(colIdLong, id) > 0;
		}

		public String getBodyById(Long id) {
			List<GmRow> rows = table.select(colBodyLong).whereColumn(colIdLong, id).rows();
			if (rows.isEmpty())
				// possible for a Queue
				return null;

			GmRow row = rows.get(0);
			String value = row.getValue(colBodyLong);
			if (value == null)
				log.warn("Unexpected null message body for id " + id + ". Table: " + table.getName());

			return value;
		}

		public int deleteExpiredMessages() {
			Long expirationCutoff = System.currentTimeMillis();
			Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

			// For now... since expiration is 0 by default, let's delete every message older than 1 day.
			String _expiration = colExpiration.getSingleSqlColumn();
			String _created = colCreated.getSingleSqlColumn();

			// @formatter:off
			return table.delete()
					.where("(" + _expiration + "< ? and " + _expiration + " != ?) or ( " + _created + " < ? )",
							                      expirationCutoff,            0,                         yesterday);
			// @formatter:on
		}

	}

	// #################################################
	// # . . . . Ensuring tables and triggers . . . . .#
	// #################################################

	public void ensureTablesAndTriggers() {
		logDebug("Ensuring tables for topics and queues");
		tableTopic.table.ensure();
		tableQueue.table.ensure();

		ensureTriggerFunction();
		ensureTrigger(true);
		ensureTrigger(false);
	}

	private void ensureTriggerFunction() {
		// Notice the tableName::body::nodeId::... will be passed as a parameter
		String createFunctionSQL = """
						CREATE OR REPLACE FUNCTION FUNCTION_NAME() RETURNS TRIGGER AS $$
						BEGIN
							PERFORM pg_notify('new_message', jsonb_build_object(
								'table', TG_TABLE_NAME,
								'id', NEW.id,
								'body', NEW.bodyShort,
								'dstName', NEW.dstName,
								'nodeId', NEW.nodeId,
								'appId', NEW.appId,
								'exp', NEW.expiration
							)::text);
							RETURN NEW;
						END;
						$$ LANGUAGE plpgsql;
				""".replace("FUNCTION_NAME", triggerFunctionName());

		JdbcTools.withStatement(dataSource, () -> "Creating FUNCTION notify_msg()", stmt -> {
			stmt.execute(createFunctionSQL);
		});
	}

	private void ensureTrigger(boolean isTopic) {
		String tableName = isTopic ? tableTopic.table.getName() : tableQueue.table.getName();
		String letter = isTopic ? "t" : "q";
		String triggerName = sqlPrefix + "_after_insert_" + letter;

		String createTriggerSQL = """
						DO $$
						BEGIN
							IF NOT EXISTS (
								SELECT 1
								FROM pg_trigger
								WHERE tgname = 'TRIGGER_NAME'
								AND tgrelid = 'TABLE_NAME'::regclass
							) THEN
								CREATE TRIGGER TRIGGER_NAME
								AFTER INSERT ON TABLE_NAME
								FOR EACH ROW EXECUTE FUNCTION FUNCTION_NAME();
							END IF;
						END;
						$$
				""" //
				.replace("TABLE_NAME", tableName) //
				.replace("TRIGGER_NAME", triggerName) //
				.replace("FUNCTION_NAME", triggerFunctionName()) //
		;

		JdbcTools.withStatement(dataSource, () -> "Creating TRIGGER after_insert", stmt -> {
			stmt.execute(createTriggerSQL);
		});
	}

	private String triggerFunctionName() {
		return sqlPrefix + "_notify_msg";
	}

	// #################################################
	// # . . . . . . . . Send message . . . . . . . . .#
	// #################################################

	public void sendMessage(JdbcMessageEnvelope envelope, Destination destination) {
		if (destination instanceof Topic)
			tableTopic.sendMessage(envelope, destination);
		else
			tableQueue.sendMessage(envelope, destination);
	}

	// #################################################
	// # . . . . Ensuring notification listeners . . . #
	// #################################################

	private Thread listenerThread;

	/**
	 * Starts a listener thread for topic and queue inserts.
	 * <p>
	 * This listener also dispatches the message to all relevant consumers in the {@link MessageNotificationDispatcher#run()} method.
	 * <p>
	 * Queue messages are only dispatched if they are successfully deleted, thus guaranteeing only a single node will receive such a message.
	 **/
	public void ensureMsgListenerThreadsRunning() {
		if (listenerThread == null)
			ensureMsgListenerThreadsRunningSync();
	}

	private synchronized void ensureMsgListenerThreadsRunningSync() {
		if (listenerThread == null)
			listenerThread = Thread.ofVirtual() //
					.name("Jdbc Msg Listener (" + identifier + ")") //
					.start(newMsgListener);
	}

	class JdbcMsgListener implements Runnable {

		/* package */ volatile boolean running = true;

		private final ObjectMapper mapper = new ObjectMapper();

		private final int MIN_RETRY_SECONDS = 1;
		private final int MAX_RETRY_SECONDS = 10 * 60; // 10 minutes

		private int retrySeconds = MIN_RETRY_SECONDS;

		@Override
		public void run() {
			while (true) {

				try (Connection conn = dataSource.getConnection()) {
					listen(conn);

				} catch (Exception e) {

					if (Thread.currentThread().isInterrupted()) {
						logThreadInterrupted();
						return;
					}

					log.warn(Thread.currentThread().getName() + " encountered an error. Retry in " + retrySeconds + " seconds.", e);

					try {
						Thread.sleep(retrySeconds * 1000L);
					} catch (InterruptedException ignored) {
						logThreadInterrupted();
						return;
					}

					retrySeconds = Math.min(2 * retrySeconds, MAX_RETRY_SECONDS);
				}

			}
		}

		private void logThreadInterrupted() {
			log.info(Thread.currentThread().getName() + " thread interrupted. Shutting down.");
		}

		private void listen(Connection conn) throws SQLException {
			Statement stmt = conn.createStatement();
			stmt.execute("LISTEN new_message"); // Subscribe to the channel
			stmt.close();

			org.postgresql.PGConnection pgConn = conn.unwrap(org.postgresql.PGConnection.class);

			log.info(Thread.currentThread().getName() + " is now listening to new messages.");
			retrySeconds = MIN_RETRY_SECONDS;

			while (running) {
				// Check for notifications
				org.postgresql.PGNotification[] notifications = pgConn.getNotifications(0);
				if (notifications == null)
					continue;

				for (org.postgresql.PGNotification notification : notifications)
					Thread.ofVirtual() //
							.name("JDBC Message Dispatcher" + notificationCounter.getAndIncrement()) //
							.start(new MessageNotificationDispatcher(notification.getParameter()));
			}
		}

		// #################################################
		// # . . . . . . . Message Dispatching . . . . . . #
		// #################################################

		class MessageNotificationDispatcher implements Runnable {

			private final String parameterJson;

			private Map<String, Object> jsonMap;
			private boolean isTopic;
			private Long id;
			private String dstName;
			private MsgTable table;

			private Set<JdbcMessageConsumer> consumers;

			/** For parameter format see {@link JdbcMsgGmDb#ensureTriggerFunction()} above. */
			public MessageNotificationDispatcher(String parameterJson) {
				this.parameterJson = parameterJson;
			}

			@Override
			public void run() {
				if (StringTools.isEmpty(parameterJson)) {
					log.warn("Somehow got notified with new message, but the parameter is empty.");
					return;
				}

				try {
					tryDispatchMessageNotification();

				} catch (Exception e) {
					log.warn("Error while dispatching new message notification: " + parameterJson, e);
				}
			}

			private void tryDispatchMessageNotification() {
				if (!parseNotification())
					return;

				if (!loadConsumers())
					return;

				if (!messageAcceptable())
					return;

				Message message = resolveMessage();
				if (message == null)
					return;

				// For queue only the one dispatcher that manages to delete it has the right to pass it to a consumer
				if (!isTopic)
					if (!table.deleteMessage(id))
						return;

				for (JdbcMessageConsumer consumer : consumers) {
					executor.submit(() -> consumer.receivedMessage(dstName, message));

					if (!isTopic)
						return;
				}
			}

			private boolean parseNotification() {
				// parse the message
				jsonMap = parseJson();
				if (jsonMap == null)
					return false;

				// load values
				isTopic = ((String) jsonMap.get("table")).endsWith("topic");
				id = getLongValue("id");
				dstName = (String) jsonMap.get("dstName");
				table = isTopic ? tableTopic : tableQueue;

				return true;
			}

			private Map<String, Object> parseJson() {
				try {
					return mapper.readValue(parameterJson, Map.class);
				} catch (JsonProcessingException e) {
					log.warn("Error while parsing new message notification: " + parameterJson, e);
					return null;
				}
			}

			private boolean loadConsumers() {
				consumers = msgConnection.getConsumersMap(isTopic).get(dstName);
				if (!isEmpty(consumers))
					return true;

				log.trace(() -> "No message consumers found. Message parameters: " + parameterJson);
				return false;
			}

			private boolean messageAcceptable() {
				if (isExpired()) {
					log.warn(() -> "Received an expired message " + parameterJson);
					return false;
				}
				if (!matchesAddressee()) {
					log.trace(() -> "Message addressee of " + parameterJson + " does not match this address.");
					return false;
				}
				return true;
			}

			private boolean isExpired() {
				Long expiration = getLongValue("expiration");

				return expiration != null && //
						expiration > 0 && expiration < System.currentTimeMillis();
			}

			private boolean matchesAddressee() {
				String addresseeAppId = (String) jsonMap.get("appId");
				String addresseeNodeId = (String) jsonMap.get("nodeId");

				String myAppId = msgContext.getApplicationId();
				String myNodeId = msgContext.getNodeId();

				log.trace(() -> "Received message for node " + addresseeNodeId + ", application " + addresseeAppId + ", local instanceId: " + myNodeId
						+ "@" + myAppId);

				return (addresseeNodeId == null || addresseeNodeId.equals(myNodeId)) && //
						(addresseeAppId == null || addresseeAppId.equals(myAppId));
			}

			private Message resolveMessage() {
				Destination dst = isTopic ? Topic.create(dstName) : Queue.create(dstName);

				String body = resolveMessageBody();
				if (body == null) {
					if (!isTopic)
						log.warn("No body found for queue message with id " + id + ". Notification: " + parameterJson);
					return null;
				}

				byte[] messageBody = Base64.getDecoder().decode(body);
				Message message = msgContext.unmarshal(messageBody);

				message.setDestination(dst);

				return message;
			}

			private String resolveMessageBody() {
				String body = (String) jsonMap.get("body");
				if (body != null)
					return body;

				return table.getBodyById(id);
			}

			private Long getLongValue(String key) {
				Object value = jsonMap.get(key);
				if (value == null)
					return null;
				if (value instanceof Long)
					return (Long) value;
				if (value instanceof Integer)
					return (long) (Integer) value;

				throw new IllegalArgumentException("Value '" + value + "' for key '" + key + "' is not an integer nor a long.");
			}

		}

	}

	// #################################################
	// # . . . . . . . . . . Misc . . . . . . . . . . .#
	// #################################################

	public void close() {
		executor.shutdown();

		gmDb.preDestroy();

		newMsgListener.running = false;

		if (listenerThread != null)
			try {
				listenerThread.interrupt();
			} catch (Exception e) {
				log.trace(() -> "Error while trying to interrupt listener thread: " + listenerThread.getName(), e);
			}
	}

	private void logDebug(String msg) {
		if (log.isDebugEnabled())
			log.debug(msg);
	}

}
