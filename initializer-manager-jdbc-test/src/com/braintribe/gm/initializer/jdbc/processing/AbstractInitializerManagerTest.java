package com.braintribe.gm.initializer.jdbc.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.gm.initializer.api.InitializerState;
import com.braintribe.gm.initializer.api.InitializerStatus;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.Reasons;

/**
 * Tests for {@link GmDbInitializerManager}
 * 
 * @author peter.gazdik
 */
public abstract class AbstractInitializerManagerTest<M extends AbstractInitializerManager> {

	protected M manager;

	@Before
	public void before() {
		manager = newManager();
	}

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	@Test
	public void noTasks_RunsWithoutError() {
		manager.runInitializers();
	}

	@Test
	public void singleTask_RunsOnce() {
		AtomicInteger counter = new AtomicInteger(0);

		manager.registerInitializer("task-a", oldFingerprint -> "v1", () -> {
			counter.incrementAndGet();
			return Maybe.complete("done");
		});

		manager.runInitializers();

		assertThat(counter.get()).isEqualTo(1);
	}

	@Test
	public void singleTask_SkippedWhenFingerprintUnchanged() {
		AtomicInteger counter = new AtomicInteger(0);

		manager.registerInitializer("task-a", oldFingerprint -> "v1", () -> {
			counter.incrementAndGet();
			return Maybe.complete("done");
		});

		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(1);

		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(1);
	}

	@Test
	public void singleTask_RerunsWhenFingerprintChanges() {
		AtomicInteger counter = new AtomicInteger(0);

		manager.registerInitializer("task-a", oldFingerprint -> "v" + counter.get(), () -> {
			counter.incrementAndGet();
			return Maybe.complete("done");
		});

		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(1);

		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(2);
	}

	@Test
	public void singleTask_RerunsFirstThenSkipped() {
		// This tests that the task is being updated properly, instead of say new rows being added into DB

		AtomicInteger counter = new AtomicInteger(0);

		manager.registerInitializer("task-a", oldFingerprint -> "v" + counter.get(), () -> {
			counter.incrementAndGet();
			return Maybe.complete("done");
		});

		// sets fingerprint to v0
		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(1);

		// sets fingerprint to v1
		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(2);

		// sets fingerprint to v2
		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(3);

		manager = newManager();
		// fingerprint is v2 -> won't run
		manager.registerInitializer("task-a", oldFingerprint -> "v2", () -> {
			counter.incrementAndGet();
			return Maybe.complete("done");
		});
		manager.runInitializers();

		assertThat(counter.get()).isEqualTo(3);

	}

	@Test
	public void taskOrdering_DependencyRunsFirst() {
		List<String> executionOrder = new ArrayList<>();

		manager.registerInitializer("task-b", old -> "v1", () -> {
			executionOrder.add("task-b");
			return Maybe.complete(null);
		});

		manager.registerInitializer("task-a", old -> "v1", () -> {
			executionOrder.add("task-a");
			return Maybe.complete(null);
		});

		manager.registerInitializer("task-c", old -> "v1", () -> {
			executionOrder.add("task-c");
			return Maybe.complete(null);
		});

		manager.ensureOrder("task-a", "task-b");
		manager.ensureOrder("task-b", "task-c");

		manager.runInitializers();

		assertThat(executionOrder).containsExactly("task-a", "task-b", "task-c");
	}

	@Test
	public void taskOrdering_CycleDetected() {
		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.complete(null));
		manager.registerInitializer("task-b", old -> "v1", () -> Maybe.complete(null));

		manager.ensureOrder("task-a", "task-b");
		manager.ensureOrder("task-b", "task-a");

		assertThatThrownBy(manager::runInitializers) //
				.isInstanceOf(IllegalStateException.class) //
				.hasMessageContaining("Cycle detected");
	}

	@Test
	public void failingTask_DoesNotUpdateFingerprint() {
		AtomicInteger counter = new AtomicInteger(0);

		manager.registerInitializer("task-a", old -> "v1", () -> {
			counter.incrementAndGet();
			return Maybe.empty(Reasons.build(Reason.T).text("test failure").toReason());
		});

		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(1);

		// Run again - should run again because fingerprint was not updated on failure
		manager = newManager();
		manager.registerInitializer("task-a", old -> "v1", () -> {
			counter.incrementAndGet();
			return Maybe.complete("now it works");
		});

		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(2);
	}

	@Test
	public void exceptionInTask_DoesNotUpdateFingerprint() {
		AtomicInteger counter = new AtomicInteger(0);

		manager.registerInitializer("task-a", old -> "v1", () -> {
			counter.incrementAndGet();
			throw new RuntimeException("Simulated failure");
		});

		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(1);

		// Run again - should run again because fingerprint was not updated on exception
		manager = newManager();
		manager.registerInitializer("task-a", old -> "v1", () -> {
			counter.incrementAndGet();
			return Maybe.complete("recovered");
		});

		manager.runInitializers();
		assertThat(counter.get()).isEqualTo(2);
	}

	@Test
	public void initializerNames_AreAllRegisteredTasks() {
		assertThat(manager.initializerNames()).isEmpty();

		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.complete(null));
		manager.registerInitializer("task-b", old -> "v1", () -> Maybe.complete(null));

		assertThat(manager.initializerNames()).containsExactlyInAnyOrder("task-a", "task-b");
	}

	@Test
	public void statuses_RegisteredTaskNeverRan() {
		InitializerStatus status = statusOf("task-a");

		assertThat(status.state()).isEqualTo(InitializerState.NEVER_RUN);
		assertThat(status.fingerprint()).isNull();
	}

	/** Only registered tasks are checked, so what an earlier run persisted for a task that is no longer registered does not matter. */
	@Test
	public void statuses_UnregisteredTaskIsNotRegistered() {
		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.empty(Reasons.build(Reason.T).text("test failure").toReason()));
		manager.runInitializers();

		InitializerStatus status = newManager().statusesOf(Arrays.asList("task-a")).get("task-a");
		assertThat(status.state()).isEqualTo(InitializerState.NOT_REGISTERED);
		assertThat(status.message()).isNull();
	}

	@Test
	public void statuses_SucceededTask() {
		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.complete("done"));
		manager.runInitializers();

		InitializerStatus status = statusOf("task-a");
		assertThat(status.state()).isEqualTo(InitializerState.SUCCEEDED);
		assertThat(status.fingerprint()).isEqualTo("v1");
		assertThat(status.updated()).isNotNull();
		assertThat(status.message()).contains("done");
	}

	@Test
	public void statuses_FailedTaskHasReason() {
		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.empty(Reasons.build(Reason.T).text("test failure").toReason()));
		manager.runInitializers();

		InitializerStatus status = statusOf("task-a");
		assertThat(status.state()).isEqualTo(InitializerState.FAILED);
		assertThat(status.fingerprint()).isNull();
		assertThat(status.updated()).isNotNull();
		assertThat(status.message()).contains("test failure");
	}

	@Test
	public void statuses_ExceptionInTaskIsFailure() {
		manager.registerInitializer("task-a", old -> "v1", () -> {
			throw new RuntimeException("Simulated failure");
		});
		manager.runInitializers();

		InitializerStatus status = statusOf("task-a");
		assertThat(status.state()).isEqualTo(InitializerState.FAILED);
		assertThat(status.message()).contains("RuntimeException", "Simulated failure");
	}

	/** The failure of an earlier run, e.g. on another node, must not survive a later successful run. */
	@Test
	public void statuses_LaterSuccessReplacesFailure() {
		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.empty(Reasons.build(Reason.T).text("test failure").toReason()));
		manager.runInitializers();

		manager = newManager();
		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.complete("recovered"));
		manager.runInitializers();

		InitializerStatus status = statusOf("task-a");
		assertThat(status.state()).isEqualTo(InitializerState.SUCCEEDED);
		assertThat(status.fingerprint()).isEqualTo("v1");
		assertThat(status.message()).doesNotContain("test failure");
	}

	/**
	 * The stored fingerprint is that of the last successful run. A skip with that fingerprint therefore means that the configuration is back to the
	 * one that succeeded, and the failure in between no longer applies.
	 */
	@Test
	public void statuses_SkipWithFingerprintOfLastSuccessReplacesFailure() {
		AtomicInteger counter = new AtomicInteger(0);

		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.complete("done"));
		manager.runInitializers();

		manager = newManager();
		manager.registerInitializer("task-a", old -> "v2", () -> Maybe.empty(Reasons.build(Reason.T).text("test failure").toReason()));
		manager.runInitializers();
		assertThat(statusOf("task-a").state()).isEqualTo(InitializerState.FAILED);

		manager = newManager();
		manager.registerInitializer("task-a", old -> "v1", () -> {
			counter.incrementAndGet();
			return Maybe.complete("unexpected run");
		});
		manager.runInitializers();
		assertThat(counter.get()).isZero();

		InitializerStatus status = statusOf("task-a");
		assertThat(status.state()).isEqualTo(InitializerState.SUCCEEDED);
		assertThat(status.fingerprint()).isEqualTo("v1");
		assertThat(status.message()).contains("skipped").doesNotContain("test failure");
	}

	/** A skip after a success writes nothing, so the status still describes the run that succeeded. */
	@Test
	public void statuses_SkipAfterSuccessKeepsStatus() {
		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.complete("done"));
		manager.runInitializers();
		InitializerStatus before = statusOf("task-a");

		manager = newManager();
		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.complete("unexpected run"));
		manager.runInitializers();

		InitializerStatus status = statusOf("task-a");
		assertThat(status.state()).isEqualTo(InitializerState.SUCCEEDED);
		assertThat(status.message()).contains("done").doesNotContain("skipped");
		assertThat(status.updated()).isEqualTo(before.updated());
	}

	@Test
	public void statuses_InGivenOrder() {
		manager.registerInitializer("task-a", old -> "v1", () -> Maybe.complete(null));
		manager.registerInitializer("task-c", old -> "v1", () -> Maybe.complete(null));
		manager.runInitializers();

		assertThat(manager.statusesOf(Arrays.asList("task-c", "task-b", "task-a")).keySet()).containsExactly("task-c", "task-b", "task-a");
	}

	/**
	 * Reads the status with a new manager, i.e. from what the earlier runs persisted, like another node would. That node registers the same task, but
	 * does not run it.
	 */
	private InitializerStatus statusOf(String taskName) {
		M otherNode = newManager();
		otherNode.registerInitializer(taskName, old -> "unused", () -> Maybe.complete(null));

		return otherNode.statusesOf(Arrays.asList(taskName)).get(taskName);
	}

	protected abstract M newManager();

}
