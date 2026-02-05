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

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.execution.virtual.VirtualThreadExecutorBuilder;
import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Topic;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageListener;
import com.braintribe.transport.messaging.api.MessageProperties;
import com.braintribe.transport.messaging.api.MessagingComponentStatus;
import com.braintribe.transport.messaging.api.MessagingException;

/**
 * @see MessageConsumer
 */
/* package */ class BqMessageConsumer extends BqMessageHandler implements MessageConsumer {

	private final String consumerId;

	private MessageListener messageListener;
	private final ReentrantLock messageListenerLock = new ReentrantLock();

	private MessagingComponentStatus status = MessagingComponentStatus.OPEN;

	private final ClassLoader consumerCreationClassLoader;

	private ClassLoader listenerSettingClassLoader;

	private ExecutorService asyncListenerExecutor;
	private Future<Boolean> asyncListenerFuture;
	private final long asyncListenerTimeout = 2;
	private final TimeUnit asyncListenerTimeoutUnit = TimeUnit.SECONDS;

	private static final Logger log = Logger.getLogger(BqMessageConsumer.class);

	public BqMessageConsumer(String consumerId) throws MessagingException {
		this.consumerId = consumerId;
		this.consumerCreationClassLoader = obtainCurrentContextClassLoader();
	}

	@Override
	public MessageListener getMessageListener() throws MessagingException {
		return this.messageListener;
	}

	@Override
	public void setMessageListener(MessageListener messageListener) throws MessagingException {
		messageListenerLock.lock();
		try {
			if (messageListener == null) { // Setting to null is the equivalent of unsetting the listener for this consumer.
				shutdownAsyncListener();
				this.messageListener = null;
				return;
			}

			if (this.messageListener != null) {
				throw new MessagingException("Message listener already set for this consumer");
			}

			this.messageListener = messageListener;
			this.listenerSettingClassLoader = obtainCurrentContextClassLoader();

			if (asyncListenerExecutor == null) {
				asyncListenerExecutor = VirtualThreadExecutorBuilder.newPool() //
						.concurrency(1) //
						.description("Blocking Queue Message Consumer (" + getDestination().getName() + ")") //
						.build();
			}

			asyncListenerFuture = asyncListenerExecutor.submit(new MessageListenerConsumer());

		} finally {
			messageListenerLock.unlock();
		}

	}

	@Override
	public Message receive() throws MessagingException {
		return receive(0);
	}

	@Override
	public Message receive(long timeout) throws MessagingException {

		if (status != MessagingComponentStatus.OPEN) {
			return null;
		}

		long expiration = timeout > 0 ? System.currentTimeMillis() + timeout : 0;

		BlockingQueue<Function<String, Object>> consumerQueue = getConsumerQueue();

		while (true) {
			Function<String, Object> bqMessage;
			try {
				bqMessage = consumerQueue.poll(500, TimeUnit.MILLISECONDS);

			} catch (InterruptedException e) {
				// Removed exception from log output. It's just frightening to see exceptions in the log
				// and there is probably no benefit from having the full stacktrace anyway.
				Thread.currentThread().interrupt();
				log.debug(() -> "" + this.consumerId + ": Polling has been interrupted.");
				return null;

			} catch (Exception e) {
				throw new MessagingException("Failed to poll consumer queue" + (e.getMessage() != null ? " :" + e.getMessage() : ""), e);
			}

			if (bqMessage != null) {

				if (status != MessagingComponentStatus.OPEN) {
					if (getDestinationType() == 'q')
						getConsumerQueue().offer(bqMessage);

					return null;
				}

				if (!isExpired(bqMessage) && matchesAddressee(bqMessage)) {
					Message message = extractMessage(bqMessage);
					if (message != null)
						return message;
				}

			}

			if (status != MessagingComponentStatus.OPEN || (expiration > 0 && expiration < System.currentTimeMillis()))
				return null;
		}

	}

	@Override
	public void close() throws MessagingException {
		if (status == MessagingComponentStatus.CLOSED) {
			log.debug(() -> "Consumer [ " + this + " ] already closed.");
			return;
		}

		status = MessagingComponentStatus.CLOSING;

		try {
			shutdownAsyncListener();

			boolean removed = true;
			if (getDestination() instanceof Topic) {
				removed = getSession().getConnection().getBqMessaging().unsubscribeTopicConsumer(getDestination().getName(), consumerId);
			}

			if (removed)
				log.debug(() -> "Unregistered consumer [ " + this + " ] from BqMessaging");
			else
				log.warn("Unregistration of consumer [ " + this + " ] from BqMessaging failed.");

		} catch (Exception e) {
			throw new MessagingException("Failed to remove consumer [ " + this + " ] from BqMessaging: " + e.getMessage(), e);
		}

		status = MessagingComponentStatus.CLOSED;

		log.debug(() -> "Closed consumer [ " + this + " ]");
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "id=" + consumerId + "," + "destination="
				+ (getDestination() != null ? getDestination().getName() : "") + "," + "status=" + status + "}";
	}

	public String getConsumerId() {
		return consumerId;
	}

	protected BlockingQueue<Function<String, Object>> getConsumerQueue() {
		return getSession() //
				.getConnection() //
				.getBqMessaging(). //
				getQueue(getDestinationType(), getDestination().getName(), consumerId);
	}

	protected Message extractMessage(Function<String, Object> bqMessage) {
		Thread currentThread = Thread.currentThread();
		ClassLoader originalClassLoader = currentThread.getContextClassLoader();

		pushConsumerContextClassLoader(currentThread, originalClassLoader);

		try {
			try {
				byte[] messageData = BqMessageProperty.get(bqMessage, BqMessageProperty.message);
				Message message = getSession().getMessagingContext().unmarshallMessage(messageData);
				getSession().getMessagingContext().enrichInbound(message);

				return message;

			} catch (MarshallException e) {
				log.error("Failed to extract " + Message.class.getName() + " from [ " + bqMessage + " ]: " + e.getMessage(), e);
			} catch (Exception e) {
				log.error("Failed to get " + Message.class.getName() + " contents from [ " + bqMessage + " ]: " + e.getMessage(), e);
			}
		} finally {
			pushOriginalContextClassLoader(currentThread, originalClassLoader);
		}

		return null;
	}

	protected boolean isExpired(Function<String, Object> bqMessage) {
		Long expiration = BqMessageProperty.get(bqMessage, BqMessageProperty.expiration);

		if (expiration != null && expiration > 0 && expiration < System.currentTimeMillis()) {
			log.debug(() -> "Consumer received an expired message: " + bqMessage);
			return true;
		} else {
			log.trace(() -> "Consumer received an non-expired message: " + bqMessage);
			return false;
		}

	}

	protected boolean matchesAddressee(Function<String, Object> bqMessage) {
		if (getDestinationType() == 'q')
			return true;

		Map<String, Object> properties = BqMessageProperty.get(bqMessage, BqMessageProperty.properties);
		if (properties == null || properties.isEmpty())
			return true;

		String appId, nodeId;

		// @formatter:off
		return !(
					(
						(appId = (String) properties.get(MessageProperties.addreseeAppId.getName())) != null && 
						!appId.equals(getApplicationId())
					) || (
						(nodeId = (String) properties.get(MessageProperties.addreseeNodeId.getName())) != null && 
						!nodeId.equals(getNodeId())
					)
				);
		// @formatter:on
	}

	/**
	 * <p>
	 * Forces the interruption of the asynchronous listener task, if existent.
	 * 
	 * @return {@code true} if there was a running task and that was successfully terminated.
	 */
	protected boolean shutdownAsyncListener() {
		if (asyncListenerFuture == null)
			return false;

		messageListenerLock.lock();
		try {
			if (asyncListenerFuture == null)
				return false;

			try {
				Boolean asyncListenerResponse = asyncListenerFuture.get(asyncListenerTimeout, asyncListenerTimeoutUnit);

				log.trace(() -> "Asynchronous listener returned: " + asyncListenerResponse);

				asyncListenerFuture = null;

				return true;

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.debug(() -> "Thread interrupted while waiting asynchronous listener task to complete", e);
			} catch (ExecutionException e) {
				log.debug(() -> "Asynchronous listener execution failed", e);
			} catch (TimeoutException e) {
				log.debug(() -> "Asynchronous listener completition timeout (" + asyncListenerTimeout + " " + asyncListenerTimeoutUnit
						+ ") has been reached", e);
			}
		} finally {
			messageListenerLock.unlock();
		}

		return false;

	}

	/**
	 * <p>
	 * Pushes this consumer's preferable class loader as the current thread's context class loader.
	 * 
	 * @param currentThread
	 *            The Thread to have the context ClassLoader set to this consumer's ClassLoader ( {@link #getConsumerContextClassLoader()})
	 * @param currentClassLoader
	 *            The Thread's context ClassLoader prior this method call
	 */
	private void pushConsumerContextClassLoader(Thread currentThread, ClassLoader currentClassLoader) {
		ClassLoader consumerClassLoader = getConsumerContextClassLoader();

		if (consumerClassLoader != null && !consumerClassLoader.equals(currentClassLoader)) {
			currentThread.setContextClassLoader(consumerClassLoader);

			log.debug(
					() -> "Class loader of current thread did not match the preferable class loader for this consumer. Current thread context class loader is [ "
							+ currentClassLoader + " ] whereas preferable context class loader for this consumer is [ " + consumerClassLoader
							+ " ]. Current thread context class loader after pushing is [ " + currentThread.getContextClassLoader() + " ] ");
		}
	}

	/**
	 * <p>
	 * Pushes back the original context class loader to the current thread.
	 * 
	 * @param currentThread
	 *            The Thread to have the context ClassLoader set to its previous instance, given by {@code originalClassLoader}
	 * @param originalClassLoader
	 *            The ClassLoader to be set as the given Thread's ({@code currentThread}) context ClassLoader
	 */
	private static void pushOriginalContextClassLoader(Thread currentThread, ClassLoader originalClassLoader) {
		if (originalClassLoader != null && !originalClassLoader.equals(currentThread.getContextClassLoader())) {

			currentThread.setContextClassLoader(originalClassLoader);

			log.debug(() -> "Pushed back original thread's context class loader. Current thread context class loader after pushing back is [ "
					+ currentThread.getContextClassLoader() + " ] ");
		}
	}

	/**
	 * <p>
	 * Returns the best suitable {@link ClassLoader} to be pushed to Threads before unmarshalling Messages and before calling
	 * {@link MessageListener#onMessage(Message)} on the registered listener.
	 * 
	 * <p>
	 * If a non-null {@link MessageListener} was set to this {@link MessageConsumer}, the context ClassLoader of the thread which called
	 * {@link MessageConsumer#setMessageListener(MessageListener)} will be used.
	 * 
	 * <p>
	 * Otherwise, the context ClassLoader of the thread which created the {@link MessageConsumer} will be used.
	 * 
	 * @return This consumer's preferable ClassLoader
	 */
	private ClassLoader getConsumerContextClassLoader() {
		return listenerSettingClassLoader == null ? consumerCreationClassLoader : listenerSettingClassLoader;
	}

	private static ClassLoader obtainCurrentContextClassLoader() throws MessagingException {
		return Thread.currentThread().getContextClassLoader();
	}

	protected class MessageListenerConsumer implements Callable<Boolean> {

		@Override
		public Boolean call() {
			if (status != MessagingComponentStatus.OPEN)
				return false;

			while (true) {
				try {
					Message message = receive();

					if (status != MessagingComponentStatus.OPEN || message == null)
						return false;

					if (messageListener != null) {
						Thread currentThread = Thread.currentThread();
						ClassLoader originalClassLoader = currentThread.getContextClassLoader();
						pushConsumerContextClassLoader(currentThread, originalClassLoader);

						try {
							messageListener.onMessage(message);
						} catch (Exception e) {
							log.error("Error while delivering message " + message + " to the message listener: " + messageListener, e);
						} finally {
							pushOriginalContextClassLoader(currentThread, originalClassLoader);
						}
					}

				} catch (MessagingException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	protected static class ConsumerThreadFactory implements ThreadFactory {

		private final String name;

		protected ConsumerThreadFactory(String name) {
			this.name = name;
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = Thread.ofVirtual().unstarted(r);
			t.setName(name);
			if (!t.isDaemon())
				t.setDaemon(true);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);

			return t;
		}
	}

}
