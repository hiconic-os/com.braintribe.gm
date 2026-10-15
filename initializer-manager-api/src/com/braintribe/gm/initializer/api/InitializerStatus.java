package com.braintribe.gm.initializer.api;

import java.util.Date;

/**
 * The persisted status of one {@link InitializerTask}, see {@link InitializerRegistry#statusesOf(java.util.Collection)}.
 * <p>
 * It describes the last run of the task, on whichever node it ran.
 */
public class InitializerStatus {

	private final String taskName;
	private final InitializerState state;
	private final String fingerprint;
	private final Date updated;
	private final String updatedBy;
	private final String message;

	public InitializerStatus(String taskName, InitializerState state, String fingerprint, Date updated, String updatedBy, String message) {
		this.taskName = taskName;
		this.state = state;
		this.fingerprint = fingerprint;
		this.updated = updated;
		this.updatedBy = updatedBy;
		this.message = message;
	}

	/** The status of a registered task that never ran. */
	public static InitializerStatus neverRun(String taskName) {
		return new InitializerStatus(taskName, InitializerState.NEVER_RUN, null, null, null, null);
	}

	/** The status of a task that is not registered. */
	public static InitializerStatus notRegistered(String taskName) {
		return new InitializerStatus(taskName, InitializerState.NOT_REGISTERED, null, null, null, null);
	}

	public String taskName() {
		return taskName;
	}

	public InitializerState state() {
		return state;
	}

	/** The fingerprint of the last successful run, or null if the task never succeeded. */
	public String fingerprint() {
		return fingerprint;
	}

	/** The time of the last run, or null if the task never ran. */
	public Date updated() {
		return updated;
	}

	/** The node that did the last run, or null if the task never ran or the registry does not know the node. */
	public String updatedBy() {
		return updatedBy;
	}

	/** The note of a success, or the reason or the stack trace of a failure. May be null. */
	public String message() {
		return message;
	}

	@Override
	public String toString() {
		return "InitializerStatus[" + taskName + ": " + state + "]";
	}

}
