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

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.braintribe.model.processing.locking.db.impl.DbLocking.DbRwLock;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.utils.CollectionTools;

/**
 * @author peter.gazdik
 */
/* package */ class DbLockRefresher {

	private static final int UPDATE_BATCH_SIZE = 100;

	private final DbLocking dbLocking;

	private final List<DbRwLock> s_locks = newList();
	private volatile int nLocks = 0;

	public DbLockRefresher(DbLocking dbLocking) {
		this.dbLocking = dbLocking;
	}

	public synchronized void startRefreshing(DbRwLock rwLock) {
		s_locks.add(rwLock);
		nLocks++;
	}

	public synchronized void stopRefreshing(DbRwLock rwLock) {
		s_locks.remove(rwLock);
		nLocks--;
	}

	public void refreshLockedLocks() {
		if (nLocks == 0)
			return;

		List<DbRwLock> locksToRefresh = locksToRefresh();
		if (locksToRefresh.isEmpty())
			return;

		refresh(locksToRefresh);
	}

	private synchronized List<DbRwLock> locksToRefresh() {
		return newList(s_locks);
	}

	private void refresh(List<DbRwLock> locksToRefresh) {
		Set<String> lockIds = locksToRefresh.stream() //
				.map(dbRwLock -> dbRwLock.id) //
				.collect(Collectors.toSet());

		long current = System.currentTimeMillis();
		long expires = current + dbLocking.lockExpirationInMs;

		Timestamp expiresTs = new Timestamp(expires);

		List<List<String>> lockIdBatches = CollectionTools.split(lockIds, UPDATE_BATCH_SIZE);

		JdbcTools.withConnection(dbLocking.dataSource, true, () -> "Refreshing locks " + lockIds, c -> {

			for (List<String> lockIdBatch : lockIdBatches) {
				String sql = "update " + DbLocking.DB_TABLE_NAME + " set expires = ? where id in " + JdbcTools.questionMarks(lockIdBatch.size());

				JdbcTools.withPreparedStatement(c, sql, () -> "", ps -> {
					ps.setTimestamp(1, expiresTs);

					int i = 2;
					for (String lockId : lockIdBatch)
						ps.setString(i++, lockId);

					ps.executeUpdate();
				});
			}

		});
	}

}
