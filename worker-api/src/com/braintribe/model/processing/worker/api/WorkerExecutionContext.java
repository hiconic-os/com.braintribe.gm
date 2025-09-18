// ============================================================================
package com.braintribe.model.processing.worker.api;

import com.braintribe.logging.Logger;

/**
 * This is a helper class to be used by the {@link WorkerManager} implementations to provide a meaningful thread name and push the same information to
 * the logging NDC.
 */
public class WorkerExecutionContext {

	protected static Logger logger = Logger.getLogger(WorkerExecutionContext.class);

	private String context;
	private String originalThreadName;
	private final int maxLength = 100;

	public WorkerExecutionContext(String prefix, Object executable) {
		context = prefix != null ? (prefix + ">") : "";
		context += executable != null ? executable.toString() : "unknown";
		int idx = context.indexOf("$$Lambda$");
		if (idx > 0) {
			context = context.substring(0, idx);
		}
		if (context.length() > maxLength) {
			context = context.substring(0, maxLength);
		}
	}

	public void push() {
		originalThreadName = Thread.currentThread().getName();
		context = originalThreadName + ">" + context;
		logger.pushContext(context);
		Thread.currentThread().setName(context);
	}

	public void pop() {
		if (originalThreadName != null) {
			Thread.currentThread().setName(originalThreadName);
			logger.popContext();
		}
	}

	@Override
	public String toString() {
		return context;
	}

}
