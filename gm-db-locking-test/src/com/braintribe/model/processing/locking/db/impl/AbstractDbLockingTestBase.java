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
package com.braintribe.model.processing.locking.db.impl;

import java.io.File;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.braintribe.common.db.DbVendor;
import com.braintribe.common.db.DerbySupportingDbTestSession;
import com.braintribe.common.db.wire.contract.DbTestDataSourcesContract;
import com.braintribe.model.processing.lock.api.ReentrableReadWriteLock;
import com.braintribe.model.processing.locking.db.test.wire.contract.DbLockingTestContract;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.util.network.NetworkTools;
import com.braintribe.utils.FileTools;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;

/**
 * @author peter.gazdik
 */
@RunWith(Parameterized.class)
public abstract class AbstractDbLockingTestBase {

	// ###############################################
	// ## . . . . . . . . Static . . . . . . . . . .##
	// ###############################################

	protected static final String REENTRANCE_ID = "reentrance-id";
	protected static final String LOCK_ID = "test-use-case";

	protected static final int TIMEOUT_MS = 5000;

	private static DerbySupportingDbTestSession dbTestSession;

	private static WireContext<DbLockingTestContract> lockingWireContext;

	@BeforeClass
	public static void beforeClass() throws Exception {
		FileTools.deleteDirectoryRecursively(new File("res"));

		dbTestSession = DerbySupportingDbTestSession.startDbTest();

		lockingWireContext = Wire.context(DbLockingTestContract.class) //
				.bindContracts(DbLockingTestContract.class) //
				.bindContract(DbTestDataSourcesContract.class, dbTestSession.contract) //
				.build();

		// Call this on startup so that the result is cached and does not interfere with other tests that rely on timing
		// TODO find out if needed
		NetworkTools.getNetworkAddress().getHostAddress();
	}

	@Before
	public void setup() throws Exception {
		JdbcTools.withStatement(dataSource, () -> "Cleaning up " + DbLocking.DB_TABLE_NAME + " table", ps -> {
			ps.executeUpdate("delete from " + DbLocking.DB_TABLE_NAME);
		});
	}

	@AfterClass
	public static void afterClass() throws Exception {
		dbTestSession.shutdownDbTest();
		lockingWireContext.shutdown();
	}

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	@Parameters
	public static Object[][] params() {
		return new Object[][] { //
				{ DbVendor.derby }, //
				// { DbVendor.postgres }, //
				// { DbVendor.mysql }, //
				// { DbVendor.mssql }, //
				// { DbVendor.oracle }, //
		};
	}

	protected final DbVendor vendor;
	protected final DbLocking locking;
	protected final DataSource dataSource;

	public AbstractDbLockingTestBase(DbVendor vendor) {
		this.vendor = vendor;
		this.dataSource = dbTestSession.contract.dataSource(vendor);
		this.locking = lockingWireContext.contract().locking(vendor);
	}

	protected ReentrableReadWriteLock newReentrantLock() {
		return locking.withReentranceId(REENTRANCE_ID).forIdentifier(LOCK_ID);
	}

	protected ReentrableReadWriteLock newRandomReentrantLock() {
		return locking.forIdentifier(LOCK_ID);
	}

}
