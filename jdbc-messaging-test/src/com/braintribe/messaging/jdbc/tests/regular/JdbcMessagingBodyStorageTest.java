package com.braintribe.messaging.jdbc.tests.regular;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.exception.Exceptions;
import com.braintribe.messaging.jdbc.JdbcMessageEnvelope;
import com.braintribe.messaging.jdbc.model.MessagePayload;
import com.braintribe.messaging.jdbc.tests.JdbcMessagingInstance;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Queue;
import com.braintribe.model.messaging.Topic;
import com.braintribe.model.resource.Resource;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.utils.IOTools;
import com.braintribe.utils.RandomTools;

/**
 * Tests how a message body is stored, i.e. that a small one travels inline within the notification while a big one is streamed through a BLOB, and
 * that either way it arrives unchanged.
 * 
 * @author peter.gazdik
 */
@Category(SpecialEnvironment.class)
public class JdbcMessagingBodyStorageTest {

	/** Big enough that the marshalled message cannot possibly fit within {@link JdbcMessageEnvelope#INLINE_BODY_CHARS_LIMIT}. */
	private static final int BIG_ATTACHMENT = 500_000;

	private static final int RECEIVE_TIMEOUT = 20_000;

	private static JdbcMessagingInstance msgInstance;
	private static DataSource dataSource;

	@BeforeClass
	public static void setup() {
		msgInstance = new JdbcMessagingInstance("node-body-storage");
		dataSource = msgInstance.dbSession.contract.postgres(65432);
	}

	@AfterClass
	public static void tearDown() {
		msgInstance.shutDown();
	}

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	@Test
	public void smallMessageIsStoredInline() throws Exception {
		Topic topic = Topic.create(uniqueName("small"));

		MessageConsumer consumer = msgInstance.session.createMessageConsumer(topic);
		send(topic, createMessage("Hello", null));

		Message received = receive(consumer);
		assertThat(payloadOf(received).getText()).isEqualTo("Hello");

		assertStoredAs("topic", topic.getName(), true);
	}

	@Test
	public void bigMessageIsStoredAsBlobAndArrivesUnchanged() throws Exception {
		Topic topic = Topic.create(uniqueName("big"));
		byte[] attachment = bytes(BIG_ATTACHMENT);

		MessageConsumer consumer = msgInstance.session.createMessageConsumer(topic);
		send(topic, createMessage("Big", attachment));

		Message received = receive(consumer);
		MessagePayload payload = payloadOf(received);

		assertThat(payload.getText()).isEqualTo("Big");
		assertThat(payload.getAttachment()).describedAs("The attachment was not transferred at all.").isNotNull();
		assertThat(bytesOf(payload.getAttachment())).containsExactly(attachment);

		assertStoredAs("topic", topic.getName(), false);
	}

	/**
	 * A queue message is deleted once it was delivered, which must take its Large Object with it. This is the same cleanup as in PgBlobCleanup_Tests,
	 * but here it is triggered by the messaging itself rather than by an explicit delete.
	 */
	@Test
	public void deliveredQueueMessageLeavesNoLargeObject() throws Exception {
		Queue queue = Queue.create(uniqueName("queueBig"));

		long largeObjectsBefore = largeObjectCount();

		MessageConsumer consumer = msgInstance.session.createMessageConsumer(queue);
		send(queue, createMessage("Big", bytes(BIG_ATTACHMENT)));

		Message received = receive(consumer);
		assertThat(payloadOf(received).getText()).isEqualTo("Big");

		// The row is deleted before the message is handed over, so by now the Large Object must be gone too
		assertThat(largeObjectCount()) //
				.describedAs("Delivering a queue message left its Large Object behind.") //
				.isEqualTo(largeObjectsBefore);
	}

	// ###############################################
	// ## . . . . . . . . Helpers . . . . . . . . . ##
	// ###############################################

	private void send(com.braintribe.model.messaging.Destination destination, Message message) {
		MessageProducer producer = msgInstance.session.createMessageProducer(destination);
		try {
			producer.sendMessage(message);
		} finally {
			producer.close();
		}
	}

	private Message receive(MessageConsumer consumer) {
		Message result = consumer.receive(RECEIVE_TIMEOUT);
		assertThat(result).describedAs("No message received within " + RECEIVE_TIMEOUT + " ms.").isNotNull();

		return result;
	}

	private MessagePayload payloadOf(Message message) {
		assertThat(message.getBody()).isInstanceOf(MessagePayload.class);
		return (MessagePayload) message.getBody();
	}

	private Message createMessage(String text, byte[] attachment) {
		MessagePayload payload = MessagePayload.create(text);

		if (attachment != null) {
			Resource resource = Resource.createTransient(() -> new ByteArrayInputStream(attachment));
			resource.setName("attachment");
			resource.setMimeType("application/octet-stream");
			resource.setFileSize((long) attachment.length);

			payload.setAttachment(resource);
		}

		Message result = Message.T.create();
		result.setBody(payload);

		return result;
	}

	/** Asserts which of the two body columns the message ended up in. */
	private void assertStoredAs(String topicOrQueue, String destinationName, boolean expectedInline) {
		String table = "hc_msg_" + topicOrQueue;
		String sql = "select bodyInline is not null, bodyBlob_blob is not null from " + table + " where dstName = '" + destinationName + "'";

		boolean[] found = { false };
		boolean[] inline = { false };
		boolean[] blob = { false };

		JdbcTools.withStatement(dataSource, () -> "Checking body storage", s -> {
			try (ResultSet rs = s.executeQuery(sql)) {
				if (rs.next()) {
					found[0] = true;
					inline[0] = rs.getBoolean(1);
					blob[0] = rs.getBoolean(2);
				}
			}
		});

		assertThat(found[0]).describedAs("No row found in " + table + " for destination " + destinationName).isTrue();
		assertThat(inline[0]).describedAs("Unexpected inline body").isEqualTo(expectedInline);
		assertThat(blob[0]).describedAs("Unexpected BLOB body").isEqualTo(!expectedInline);
	}

	private long largeObjectCount() {
		long[] result = { -1 };

		JdbcTools.withStatement(dataSource, () -> "Counting large objects", s -> {
			try (ResultSet rs = s.executeQuery("select count(*) from pg_largeobject_metadata")) {
				rs.next();
				result[0] = rs.getLong(1);
			}
		});

		return result[0];
	}

	private static String uniqueName(String prefix) {
		return prefix + "-" + RandomTools.newStandardUuid();
	}

	private static byte[] bytes(int size) {
		byte[] result = new byte[size];
		for (int i = 0; i < size; i++)
			result[i] = (byte) i;

		return result;
	}

	private static byte[] bytesOf(Resource resource) {
		try (InputStream in = resource.openStream()) {
			return IOTools.slurpBytes(in);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

}
