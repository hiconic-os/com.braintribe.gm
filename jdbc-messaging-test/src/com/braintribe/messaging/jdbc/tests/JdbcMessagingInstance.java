package com.braintribe.messaging.jdbc.tests;

import java.util.UUID;

import com.braintribe.codec.marshaller.bin.Bin2Marshaller;
import com.braintribe.common.db.BasicDbTestSession;
import com.braintribe.common.db.wire.contract.DbTestDataSourcesContract;
import com.braintribe.messaging.jdbc.JdbcConnectionProvider;
import com.braintribe.messaging.jdbc.JdbcMsgConnection;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingSession;

/**
 * This requires a running Postgres DB, see {@link DbTestDataSourcesContract#postgres()}
 * 
 * @author peter.gazdik
 */
public class JdbcMessagingInstance {

	public static final String APPLICATION_ID = "unit-test";

	public final BasicDbTestSession dbSession;
	public final MessagingContext messagingContext;

	public final JdbcConnectionProvider connectionProvider;
	public final JdbcMsgConnection connection;
	public final MessagingSession session;

	private static final int POSTGRES_PORT = 65432;

	public JdbcMessagingInstance() {
		this("node-" + UUID.randomUUID().toString());
	}

	public JdbcMessagingInstance(String nodeId) {
		dbSession = BasicDbTestSession.startDbTest();

		messagingContext = new MessagingContext();
		messagingContext.setMarshaller(new Bin2Marshaller());
		messagingContext.setApplicationId(APPLICATION_ID);
		messagingContext.setNodeId(nodeId);

		connectionProvider = new JdbcConnectionProvider();
		connectionProvider.setName("test-messaging");
		connectionProvider.setSqlPrefix("hc");
		connectionProvider.setDataSource(dbSession.contract.postgres(POSTGRES_PORT));
		connectionProvider.setMessagingContext(messagingContext);

		connection = connectionProvider.provideMessagingConnection();

		session = connection.createMessagingSession();
	}

	public void shutDown() {
		connectionProvider.close();
		dbSession.shutdownDbTest();
	}

}
