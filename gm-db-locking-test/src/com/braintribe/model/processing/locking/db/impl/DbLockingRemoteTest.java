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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.common.db.DbVendor;
import com.braintribe.model.processing.locking.db.test.remote.JvmExecutor;
import com.braintribe.model.processing.locking.db.test.remote.RemoteProcess;
import com.braintribe.model.processing.locking.db.test.wire.contract.DbLockingTestContract;
import com.braintribe.testing.category.VerySlow;
import com.braintribe.utils.DateTools;
import com.braintribe.utils.FileTools;

/**
 * Tests for {@link DbLocking}
 * 
 * NOTE: I have no idea what this is testing, it's been copied from the old tests for DbLockManager and worked right off the bat.
 * 
 * @see DbLockingTestContract
 */
public class DbLockingRemoteTest extends AbstractDbLockingTestBase {

	public static final String IDENTIFIER = "someIdentifier";

	public DbLockingRemoteTest(DbVendor vendor) {
		super(vendor);
	}

	private final static long INTERVAL = 500L;
	private final static long LOCK_TIMEOUT = INTERVAL * 20;

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	@Test
	@Category(VerySlow.class)
	public void testRemoteJvmsWithoutFailProbability() throws Exception {

		File tempFile = File.createTempFile("number", ".txt");
		FileTools.writeStringToFile(tempFile, "0");
		try {
			int worker = 2;
			int iterations = 10;

			List<RemoteProcess> remoteProcesses = JvmExecutor.executeWorkers(worker, 0, 60_000L, tempFile.getAbsolutePath(), iterations);

			for (RemoteProcess p : remoteProcesses) {
				p.getProcess().waitFor();
			}

			String content = FileTools.readStringFromFile(tempFile);
			print("Read content: " + content);
			int number = Integer.parseInt(content);
			int expected = worker * iterations;
			print("Read number: " + number + ", Expecting " + expected);
			assertThat(number).isEqualTo(expected);
		} finally {
			FileTools.deleteFile(tempFile);
		}

	}

	@Test
	@Category(VerySlow.class)
	public void testRemoteJvmsWithFailProbability() throws Exception {

		File tempFile = File.createTempFile("number", ".txt");
		FileTools.writeStringToFile(tempFile, "0");
		try {
			int worker = 10;
			int iterations = 10;

			long wait = (worker * iterations * INTERVAL) + (LOCK_TIMEOUT * worker);

			List<RemoteProcess> remoteProcesses = JvmExecutor.executeWorkers(worker, 10, wait, tempFile.getAbsolutePath(), iterations);

			for (RemoteProcess p : remoteProcesses) {
				p.getProcess().waitFor();
			}

			String content = FileTools.readStringFromFile(tempFile);
			print("Read content: " + content);
			int number = Integer.parseInt(content);
			assertThat(number).isGreaterThanOrEqualTo(worker);
		} finally {
			FileTools.deleteFile(tempFile);
		}

	}

	private static void print(String text) {
		System.out.println(DateTools.encode(new Date(), DateTools.LEGACY_DATETIME_WITH_MS_FORMAT) + " [Main]: " + text);
	}

}
