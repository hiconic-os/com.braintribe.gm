package com.braintribe.messaging.jdbc.tests.regular;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.common.db.wire.contract.DbTestDataSourcesContract;
import com.braintribe.messaging.jdbc.model.MessagePayload;
import com.braintribe.messaging.jdbc.tests.JdbcMessagingInstance;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Queue;
import com.braintribe.model.messaging.Topic;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessageProperties;

/**
 * This requires a running Postgres DB, see {@link DbTestDataSourcesContract#postgres()}
 * 
 * @author peter.gazdik
 */
@Category(SpecialEnvironment.class)
public class JdbcMessagingTest {

	private static JdbcMessagingInstance msgInstanceA;
	private static JdbcMessagingInstance msgInstanceB;

	@BeforeClass
	public static void setup() {
		msgInstanceA = new JdbcMessagingInstance("node-a");
		msgInstanceB = new JdbcMessagingInstance("node-b");
	}

	@AfterClass
	public static void tearDown() {
		msgInstanceA.shutDown();
		msgInstanceB.shutDown();
	}

	// with 2 Messaging instances
	// send Message to Topic
	// send Message to Queue
	// assert all Topic consumers received message
	// assert just a single Queue consumer received message

	@Test(timeout = 5000L)
	public void testBasicFunctionality() throws Exception {
		Topic topic = Topic.create("T1");
		Queue queue = Queue.create("Q1");

		MessageConsumer tConsumerA1 = msgInstanceA.session.createMessageConsumer(topic);
		MessageConsumer tConsumerA2 = msgInstanceA.session.createMessageConsumer(topic);
		MessageConsumer tConsumerB1 = msgInstanceB.session.createMessageConsumer(topic);

		MessageConsumer qConsumerA1 = msgInstanceA.session.createMessageConsumer(queue);
		MessageConsumer qConsumerA2 = msgInstanceA.session.createMessageConsumer(queue);
		MessageConsumer qConsumerB1 = msgInstanceB.session.createMessageConsumer(queue);

		MessageProducer msgProducer = msgInstanceA.session.createMessageProducer();

		msgProducer.sendMessage(createMessage("TOPIC"), topic);
		msgProducer.sendMessage(createMessage("QUEUE"), queue);
		msgProducer.close();

		assertReceive(tConsumerA1, "TOPIC");
		assertReceive(tConsumerA2, "TOPIC");
		assertReceive(tConsumerB1, "TOPIC");

		assertOnlyOneReceives("QUEUE", qConsumerA1, qConsumerA2, qConsumerB1);

		assertNoMoreMessage(tConsumerA1);
		assertNoMoreMessage(tConsumerA2);
		assertNoMoreMessage(tConsumerB1);

		assertNoMoreMessage(qConsumerA1);
		assertNoMoreMessage(qConsumerA2);
		assertNoMoreMessage(qConsumerB1);

		tConsumerA1.close();
		tConsumerA2.close();
		tConsumerB1.close();
		qConsumerA1.close();
	}

	@Test(timeout = 5000L)
	public void testAdressedMessage() throws Exception {
		Topic topic = Topic.create("T1");

		MessageConsumer tConsumerA = msgInstanceA.session.createMessageConsumer(topic);
		MessageConsumer tConsumerB = msgInstanceB.session.createMessageConsumer(topic);

		MessageProducer msgProducer = msgInstanceA.session.createMessageProducer();

		String appId = JdbcMessagingInstance.APPLICATION_ID;
		String nodeId = msgInstanceB.messagingContext.getNodeId();

		msgProducer.sendMessage(createMessageTo("TOPIC", appId, nodeId), topic);
		msgProducer.close();

		assertReceive(tConsumerB, "TOPIC");

		assertNoMoreMessage(tConsumerA);
		assertNoMoreMessage(tConsumerB);

		tConsumerA.close();
		tConsumerB.close();
	}

	/**
	 * IMPORTANT!!!
	 * 
	 * This might in theory fail, if previous tests left some messages over, which expire during the run of this test. To be sure, run as a stand
	 * alone test.
	 */
	@Test(timeout = 5000L)
	public void testDeletesExpiredMessages() throws Exception {
		Topic topic = Topic.create("T1");

		MessageConsumer tConsumerA1 = msgInstanceA.session.createMessageConsumer(topic);
		MessageConsumer tConsumerB1 = msgInstanceB.session.createMessageConsumer(topic);

		// clean as much as possible
		deleteExpiredMessages();

		MessageProducer msgProducer = msgInstanceA.session.createMessageProducer();

		Message msg = createMessage("TOPIC");
		msg.setTimeToLive(100L);

		msgProducer.sendMessage(msg, topic);
		msgProducer.close();

		assertReceive(tConsumerA1, "TOPIC");
		assertReceive(tConsumerB1, "TOPIC");

		assertNoMoreMessage(tConsumerA1);
		assertNoMoreMessage(tConsumerB1);

		Thread.sleep(100);

		assertDeletesExpired(1);
		assertDeletesExpired(0);

		tConsumerA1.close();
		tConsumerB1.close();
	}

	private void assertReceive(MessageConsumer consumer, String expectedText) {
		Message msg = consumer.receive();
		assertThat(msg).isNotNull();

		MessagePayload body = (MessagePayload) msg.getBody();
		assertThat(body).isNotNull();

		assertThat(body.getText()).isEqualTo(expectedText);
	}

	// Typically the message is delivered right away, but just in case we give it up to 1.5 seconds (100 x 3 * 5 ms)
	private void assertOnlyOneReceives(String expectedText, MessageConsumer qConsumerA1, MessageConsumer qConsumerA2, MessageConsumer qConsumerB1) {
		for (int i = 0; i < 100; i++) {
			Message msgA1 = qConsumerA1.receive(5);
			Message msgA2 = qConsumerA2.receive(5);
			Message msgB1 = qConsumerB1.receive(5);

			if (msgA1 == null && msgA2 == null && msgB1 == null)
				continue;

			int received = 0;
			if (isMsg(msgA1, expectedText))
				received++;
			if (isMsg(msgA2, expectedText))
				received++;
			if (isMsg(msgB1, expectedText))
				received++;

			assertThat(received).as("Wrong number of consumers that received a Message in a Queue.").isEqualTo(1);
			return;
		}
	}

	private boolean isMsg(Message msg, String expectedText) {
		if (msg == null)
			return false;

		MessagePayload body = (MessagePayload) msg.getBody();
		assertThat(body).isNotNull();

		assertThat(body.getText()).isEqualTo(expectedText);
		return true;
	}

	private void assertNoMoreMessage(MessageConsumer consumer) {
		Message msg = consumer.receive(100);
		assertThat(msg).isNull();
	}

	private void assertDeletesExpired(int expected) {
		int deleted = deleteExpiredMessages();
		assertThat(deleted).isEqualTo(expected);
	}

	private int deleteExpiredMessages() {
		return msgInstanceA.connectionProvider.deleteExpiredMessages();
	}

	private Message createMessageTo(String text, String appId, String nodeId) {
		Message msg = createMessage(text);

		Map<String, Object> props = msg.getProperties();
		props.put(MessageProperties.addreseeAppId.getName(), appId);
		props.put(MessageProperties.addreseeNodeId.getName(), nodeId);

		return msg;
	}

	private Message createMessage(String text) {
		Message msg = Message.T.create();
		msg.setBody(MessagePayload.create(text));
		return msg;
	}

}
