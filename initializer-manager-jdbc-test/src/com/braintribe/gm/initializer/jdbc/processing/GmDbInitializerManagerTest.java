package com.braintribe.gm.initializer.jdbc.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.common.db.DbVendor;
import com.braintribe.common.db.wire.contract.DbTestDataSourcesContract;
import com.braintribe.gm.initializer.jdbc.test.wire.InitializerManagerTestWireModule;
import com.braintribe.gm.initializer.jdbc.test.wire.contract.InitializerManagerTestContract;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;

/**
 * Tests for {@link GmDbInitializerManager}
 * 
 * @author peter.gazdik
 */
public class GmDbInitializerManagerTest {

	private WireContext<InitializerManagerTestContract> wireContext;
	private GmDbInitializerManager manager;

	@Before
	public void before() {
		wireContext = Wire.context(InitializerManagerTestWireModule.INSTANCE);
		manager = newManager();
		dropTasksTable();
	}

	private void dropTasksTable() {
		DataSource dataSource = wireContext.contract(DbTestDataSourcesContract.class).dataSource(DbVendor.h2);
		JdbcTools.withStatement(dataSource, () -> "Dropping " + InitializerManagerTestContract.TABLE_NAME, ps -> {
			try {
				ps.executeUpdate("drop table " + InitializerManagerTestContract.TABLE_NAME);
			} catch (Exception e) {
				// table might not exist yet, ignore
			}
		});
	}

	@After
	public void after() {
		wireContext.shutdown();
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

	private GmDbInitializerManager newManager() {
		return wireContext.contract().newManager(DbVendor.h2);
	}

}
