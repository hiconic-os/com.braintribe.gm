package com.braintribe.gm.initializer.jdbc.processing;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.common.db.DbVendor;
import com.braintribe.common.db.wire.contract.DbTestDataSourcesContract;
import com.braintribe.gm.initializer.jdbc.test.wire.InitializerManagerTestWireModule;
import com.braintribe.gm.initializer.jdbc.test.wire.contract.InitializerManagerTestContract;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;

/**
 * Tests for {@link GmDbInitializerManager}
 * 
 * @author peter.gazdik
 */
public class GmDbInitializerManagerTest extends AbstractInitializerManagerTest<GmDbInitializerManager> {

	private WireContext<InitializerManagerTestContract> wireContext;

	@Override
	@Before
	public void before() {
		wireContext = Wire.context(InitializerManagerTestWireModule.INSTANCE);
		dropTasksTable();
		super.before();
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

	// @formatter:off
	@Override @Test public void noTasks_RunsWithoutError() { super.noTasks_RunsWithoutError(); }
	@Override @Test public void singleTask_RunsOnce() { super.singleTask_RunsOnce(); }
	@Override @Test public void singleTask_SkippedWhenFingerprintUnchanged() { super.singleTask_SkippedWhenFingerprintUnchanged(); }
	@Override @Test public void singleTask_RerunsWhenFingerprintChanges() { super.singleTask_RerunsWhenFingerprintChanges(); }
	@Override @Test public void singleTask_RerunsFirstThenSkipped() { super.singleTask_RerunsFirstThenSkipped(); }
	@Override @Test public void taskOrdering_DependencyRunsFirst() { super.taskOrdering_DependencyRunsFirst(); }
	@Override @Test public void taskOrdering_CycleDetected() { super.taskOrdering_CycleDetected(); }
	@Override @Test public void failingTask_DoesNotUpdateFingerprint() { super.failingTask_DoesNotUpdateFingerprint(); }
	@Override @Test public void exceptionInTask_DoesNotUpdateFingerprint() { super.exceptionInTask_DoesNotUpdateFingerprint(); }
	// @formatter:on

	@Test
	public void configuringDataSourceDoesNotOpenConnection() {
		AtomicInteger invocations = new AtomicInteger();
		DataSource dataSource = (DataSource) Proxy.newProxyInstance( //
				DataSource.class.getClassLoader(), //
				new Class<?>[] { DataSource.class }, //
				(proxy, method, args) -> {
					invocations.incrementAndGet();
					throw new AssertionError("DataSource must not be accessed while configuring the initializer manager: " + method.getName());
				});

		GmDbInitializerManager manager = new GmDbInitializerManager();
		manager.setDataSource(dataSource);
		manager.runInitializers(); // no registered tasks must not initialize the database either

		assertThat(invocations).hasValue(0);
	}

	@Override
	protected GmDbInitializerManager newManager() {
		return wireContext.contract().newManager(DbVendor.h2);
	}

}
