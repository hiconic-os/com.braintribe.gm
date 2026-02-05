// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.transport.messaging.bq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import com.braintribe.execution.virtual.VirtualThreadExecutorBuilder;
import com.braintribe.logging.Logger;

/* package */ class BqMessaging {

	private final Map<String, BlockingQueue<BqMessage>> queueMessages = new ConcurrentHashMap<>();
	private final Map<String, Map<String, BlockingQueue<BqMessage>>> topicMessages = new ConcurrentHashMap<>();

	private Long connectionsIdSequence = 0L;
	private final Map<Long, Long> openConnections = new HashMap<>();
	private final Map<ClassLoader, Integer> openConnectionsPerClassLoader = new HashMap<>();

	private ExecutorService reaperExecutor;
	private Future<Void> reaperExecutorFuture;
	private ClassLoader reaperClassLoader;

	private static final long reaperExecutorReturnTimeout = 2;
	private static final TimeUnit reaperExecutorReturnTimeoutUnit = TimeUnit.SECONDS;
	private static final String reaperExecutorName = "hiconic.messaging.bq.reaper";
	private static final long reaperExecutorInterval = 20 * 60 * 1000;

	private static final long retainedWarningLimit = 100000;

	private static final Logger log = Logger.getLogger(BqMessaging.class);

	public Long connect() {
		synchronized (openConnections) {
			Long connectionId = connectionsIdSequence++;

			openConnections.put(connectionId, System.currentTimeMillis());

			switchExecutorServices(connectionId, true);

			log.debug(() -> "Connection [ #" + connectionId + " ] opened. Total active connections: [ " + openConnections.size() + " ] ");

			return connectionId;
		}
	}

	public void disconnect(Long connectionId) {
		synchronized (openConnections) {
			openConnections.remove(connectionId);

			switchExecutorServices(connectionId, false);

			log.debug(() -> "Connection[ #" + connectionId + " ] closed. Total active connections: [ " + openConnections.size() + " ] ");
		}
	}

	public BlockingQueue<Function<String, Object>> getQueue(char destinationType, String destinationName, String subscriptionId) {
		BlockingQueue<BqMessage> result = requireMessagesQueue(destinationType, destinationName, subscriptionId);
		return (BlockingQueue<Function<String, Object>>) (BlockingQueue<?>) result;
	}

	public boolean subscribeTopicConsumer(String destinationName, String subscriptionId) {
		requireTopicMessagesQueue(destinationName, subscriptionId);

		return true;
	}

	// This code makes absolutely no sense
	public boolean unsubscribeTopicConsumer(String destinationName, String subscriptionId) {
		Map<String, BlockingQueue<BqMessage>> destinationRepo = topicMessages.get(destinationName);

		if (destinationRepo != null) {
			synchronized (topicMessages) {
				topicMessages.remove(subscriptionId);
			}
		}

		return true;
	}

	public void sendMessage(char destinationType, String destinationName, String msgId, byte[] msgBytes, int priority, long expiration,
			Map<String, Object> headers, Map<String, Object> properties) {

		BqMessage message = new BqMessage(destinationType, destinationName, msgId, msgBytes, priority, expiration, headers, properties);

		switch (destinationType) {
			case 't':
				sendTopicMessage(destinationName, message);
				break;
			case 'q':
				sendQueueMessage(destinationName, message);
				break;
			default:
				throw new IllegalArgumentException("Invalid destination type " + destinationType);
		}
	}

	private void sendTopicMessage(String topicName, BqMessage message) {
		Map<String, BlockingQueue<BqMessage>> destinationRepo = topicMessages.get(topicName);
		if (destinationRepo == null)
			return;

		for (BlockingQueue<BqMessage> queue : destinationRepo.values())
			queue.add(message);
	}

	private void sendQueueMessage(String queueName, BqMessage message) {
		requireMessagesQueue('q', queueName, null).add(message);
	}

	private BlockingQueue<BqMessage> requireMessagesQueue(char destinationType, String destinationName, String subscriptionId) {
		switch (destinationType) {
			case 'q':
				return requireQueueMessagesQueue(destinationName);
			case 't':
				return requireTopicMessagesQueue(destinationName, subscriptionId);
			default:
				throw new IllegalArgumentException("Unknown type of destination: " + destinationType);
		}
	}

	private BlockingQueue<BqMessage> requireQueueMessagesQueue(String destinationName) {
		return queueMessages.computeIfAbsent(destinationName, k -> new PriorityBlockingQueue<>());
	}

	private BlockingQueue<BqMessage> requireTopicMessagesQueue(String destinationName, String subscriptionId) {
		Map<String, BlockingQueue<BqMessage>> destinationRepo = topicMessages.computeIfAbsent(destinationName, k -> new ConcurrentHashMap<>());
		
		return destinationRepo.computeIfAbsent(subscriptionId, k -> new PriorityBlockingQueue<>());
	}

	/**
	 * <p>
	 * Starts, stops or restarts this component {@link ExecutorService} {@link #reaperExecutor}), based on the state of current connections.
	 * 
	 * <p>
	 * Starts if:
	 * <ul>
	 * <li>A connection is being opened and there is no {@link #reaperExecutor} initialized.</li>
	 * </ul>
	 * 
	 * <p>
	 * Stops if:
	 * <ul>
	 * <li>A connection is being closed and there are no more connections.</li>
	 * </ul>
	 * 
	 * <p>
	 * Restarts if:
	 * <ul>
	 * <li>The connection being closed is the last connection based on the class loader which last initialized {@link #reaperExecutor}.</li>
	 * </ul>
	 * 
	 */
	private void switchExecutorServices(Long connectionId, boolean opening) {

		// Instantiation and shutdown (shutdownNow()) of executors are solely controlled by this method.

		// Updating openConnectionsPerClassLoader, which keeps track of how many connections were opened by each class
		// loader.

		ClassLoader connectionClassLoader = Thread.currentThread().getContextClassLoader();
		Integer connectionsPerClassLoader = openConnectionsPerClassLoader.get(connectionClassLoader);
		if (connectionsPerClassLoader == null) {
			connectionsPerClassLoader = 0;
		}

		if (opening) {
			connectionsPerClassLoader++;
		} else if (connectionsPerClassLoader > 0) {
			connectionsPerClassLoader--;
		}

		if (connectionsPerClassLoader > 0) {
			openConnectionsPerClassLoader.put(connectionClassLoader, connectionsPerClassLoader);
		} else {
			openConnectionsPerClassLoader.remove(connectionClassLoader);
		}

		if (opening && reaperExecutor == null) {

			// Opening a connection and there is no reaperExecutor. Must start a reaper executor.

			startExecutorService(connectionClassLoader, connectionId, opening);

		} else {

			// Shuts the reaperExecutor down if there are no more connection or if the disconnecting
			// connection was the last one based on the class loader which started the current reaper.

			if (openConnections.isEmpty() || (connectionsPerClassLoader < 1 && reaperClassLoader == connectionClassLoader)) {

				if (reaperExecutor != null) {
					try {
						if (reaperExecutorFuture != null) {
							try {
								reaperExecutorFuture.get(reaperExecutorReturnTimeout, reaperExecutorReturnTimeoutUnit);
							} catch (TimeoutException t) {
								log.warn("Running  \"" + reaperExecutorName + "\" task failed to terminate after " + reaperExecutorReturnTimeout + " "
										+ reaperExecutorReturnTimeoutUnit + ".");
							}
						}

						log.debug(() -> reaperExecutorName + " stopped upon closing of the last connection [ #" + connectionId
								+ " ] using the class loader which initially started it: " + reaperClassLoader);

					} catch (Throwable e) {
						log.warn("Failed to shutdown running \"" + reaperExecutorName + "\" tasks.", e);
					}

					reaperExecutor = null;
					reaperExecutorFuture = null;

				}

				// The disconnecting connection was the last one based on the class loader which started
				// the current reaper, but there are other connections, therefore the reaper must be
				// started based on the class loader of the next active connection.

				if (!openConnections.isEmpty() && !openConnectionsPerClassLoader.isEmpty())
					startExecutorService(openConnectionsPerClassLoader.keySet().iterator().next(), connectionId, opening);
			}
		}

	}

	private void startExecutorService(ClassLoader connectionClassLoader, Long connectionId, boolean opening) {
		ClassLoader originalClassLoader = pushClassLoader(connectionClassLoader);
		try {
			reaperExecutor = VirtualThreadExecutorBuilder.newPool().concurrency(1).description("Thread Pool Message Reaper").build();
			reaperExecutorFuture = reaperExecutor.submit(new BqMessageReaper());
			reaperClassLoader = connectionClassLoader;

			log.debug(() -> reaperExecutorName + " " + (opening ? "re" : "") + "started upon " + (opening ? "opening" : "closing")
					+ " of connection [ #" + connectionId + " ] using: " + reaperClassLoader);

		} finally {
			pushClassLoader(originalClassLoader);
		}
	}

	/**
	 * Pushes the given ClassLoader as the context class loader of the current thread, returning the previous contextClassLoader.
	 * 
	 * @param newLoader
	 *            The ClassLoader to be set as the current Thread context ClassLoader
	 * @return The ClassLoader previously set as the current Thread context ClassLoader
	 */
	private static ClassLoader pushClassLoader(ClassLoader newLoader) {
		ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
		if (currentLoader != newLoader)
			Thread.currentThread().setContextClassLoader(newLoader);

		return currentLoader;
	}

	/**
	 * <p>
	 * A message wrapper which uses jdk and simple types only ({@code String}, {@code char}, {@code long} etc..).
	 * 
	 */
	private static class BqMessage implements Function<String, Object>, Callable<byte[]>, Comparable<BqMessage> {

		char destinationType;
		String destinationName;
		String messageId;
		byte[] message;
		int priority;
		long expiration;
		long producedAt;
		Map<String, Object> headers;
		Map<String, Object> properties;

		public BqMessage(char destinationType, String destinationName, String messageId, byte[] message, int priority, long expiration,
				Map<String, Object> headers, Map<String, Object> properties) {
			this.destinationType = destinationType;
			this.destinationName = destinationName;
			this.messageId = messageId;
			this.message = message;
			this.priority = priority;
			this.expiration = expiration;
			this.producedAt = System.currentTimeMillis();
			this.headers = headers;
			this.properties = properties;
		}

		@Override
		public byte[] call() throws Exception {
			return message;
		}

		@Override
		public int compareTo(BqMessage o) {

			int c = Long.compare(o.priority, this.priority);

			if (c != 0)
				return c;

			return Long.compare(this.producedAt, o.producedAt);
		}

		public boolean expired() {
			return expiration > 0 && expiration < System.currentTimeMillis();
		}

		@Override
		public String toString() {
			return "Message{id=" + messageId + ", destinationType=" + destinationType + ", destinationName=" + destinationName + ", priority="
					+ priority + ", expiration=" + expiration + "}";
		}

		@Override
		public Object apply(String propertyName) {

			Objects.requireNonNull(propertyName, "Property must not be null");

			BqMessageProperty property = BqMessageProperty.valueOf(propertyName);

			switch (property) {
				case destinationType:
					return destinationType;
				case destinationName:
					return destinationName;
				case messageId:
					return messageId;
				case message:
					return message;
				case priority:
					return priority;
				case expiration:
					return expiration;
				case expired:
					return expired();
				case producedAt:
					return producedAt;
				case headers:
					return headers;
				case properties:
					return properties;
				default:
					throw new IllegalArgumentException("Unmapped property " + propertyName);
			}

		}

	}

	/**
	 * <p>
	 * A task responsible for deleting queue-addressed messages which expired.
	 * 
	 */
	private class BqMessageReaper implements Callable<Void> {

		@Override
		public Void call() {
			log.trace(() -> "Messages reaper initialized");

			try {
				while (true) {
					log.trace(() -> "Scheduled messages reaper to run within " + reaperExecutorInterval + " ms");

					Thread.sleep(reaperExecutorInterval);
					reapExpiredMessages();
				}

			} catch (InterruptedException e) {
				log.trace(() -> "Messages reaper thread interrupted.");
			}

			return null;
		}

		private void reapExpiredMessages() {
			log.trace(() -> "Starting messages reaper");

			try {
				long l = System.currentTimeMillis();
				int r = 0;
				int s = 0;
				int n = 0;

				for (Map.Entry<String, BlockingQueue<BqMessage>> entry : queueMessages.entrySet()) {

					Iterator<BqMessage> iter = entry.getValue().iterator();

					while (iter.hasNext()) {
						BqMessage bqMessage = iter.next();
						if (bqMessage.expired()) {
							log.debug(() -> "Undelivered message will be discaded as it has expired: [ " + bqMessage + " ]");

							iter.remove();
							r++;

						} else {
							if (bqMessage.expiration == 0) {
								n++;
								log.trace(() -> "Undelivered message remained retained as it will never expire: [ " + bqMessage + " ]");

							} else {
								s++;
								log.trace(() -> "Undelivered message is not expired and will be retained: [ " + bqMessage + " ]");
							}
						}
					}

				}

				if (log.isDebugEnabled()) {
					l = System.currentTimeMillis() - l;
					log.info( //
							"Message reaper ran in " + l + " ms checking the expiration in " + (s + r + n) + " retained message(s)" //
									+ (r > 0 ? ". " + r + " expired and got discarded" : "") //
									+ (s > 0 ? ". " + s + " remained retained and will expire eventually" : "") //
									+ (n > 0 ? ". " + n + " remained retained and will never expire" : ""));

				} else if (r > 0) {
					log.info(r + " expired message(s) discarded" + (s > 0 ? ". " + s + " expirable message(s) remained retained" : "")
							+ (n > 0 ? ". " + n + " unexpirable message(s) remained retained" : ""));
				}

				if (n + s > retainedWarningLimit)
					log.warn("There are " + (n + s) + " retained messages, of which " + (s > 0 ? ". " + s + " will eventually expire" : "")
							+ (n > 0 ? ". " + n + " will never expire" : ""));

			} catch (Exception e) {
				log.error("Failed to run expired messages reaper: " + e.getMessage(), e);
			}
		}

	}

}
