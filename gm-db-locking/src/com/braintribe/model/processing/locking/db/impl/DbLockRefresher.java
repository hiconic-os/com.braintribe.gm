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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.braintribe.model.processing.locking.db.impl.DbLocking.DbRwLock;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.utils.CollectionTools;

/**
 * @author peter.gazdik
 */
/* package */ class DbLockRefresher {

	private static final int UPDATE_BATCH_SIZE = 100;

	private final DbLocking dbLocking;

	/* We update the row by id and timestamp to ensure we only ever refresh rows for locks we acquired, but not newer rows of another owner which
	 * could have been acquired if we lost connection to DB and our lock expired. */
	private record LockToRefresh(String id, Timestamp created) {
		// empty
	}

	private final List<LockToRefresh> s_locks = newList();
	private volatile int nLocks = 0;

	public DbLockRefresher(DbLocking dbLocking) {
		this.dbLocking = dbLocking;
	}

	public synchronized void startRefreshing(DbRwLock lock, Timestamp created) {
		s_locks.add(new LockToRefresh(lock.id, created));
		nLocks++;
	}

	public synchronized void stopRefreshing(DbRwLock lock, Timestamp created) {
		s_locks.remove(new LockToRefresh(lock.id, created));
		nLocks--;
	}

	public void refreshLockedLocks() {
		if (nLocks == 0)
			return;

		List<LockToRefresh> locksToRefresh = locksToRefresh();
		if (locksToRefresh.isEmpty())
			return;

		refresh(locksToRefresh);
	}

	private synchronized List<LockToRefresh> locksToRefresh() {
		return newList(s_locks);
	}

	private void refresh(List<LockToRefresh> locksToRefresh) {
		// dedup - the same incarnation may be held by multiple threads
		Set<LockToRefresh> locks = new HashSet<>(locksToRefresh);

		long current = System.currentTimeMillis();
		long expires = current + dbLocking.lockExpirationInMs;

		Timestamp expiresTs = new Timestamp(expires);

		List<List<LockToRefresh>> lockBatches = CollectionTools.split(locks, UPDATE_BATCH_SIZE);

		String sql = "update " + DbLocking.DB_TABLE_NAME + " set expires = ? where id = ? and created = ?";

		JdbcTools.withConnection(dbLocking.dataSource, true, () -> "Refreshing " + locks.size() + " locks", c -> {

			JdbcTools.withPreparedStatement(c, sql, () -> "", ps -> {
				for (List<LockToRefresh> lockBatch : lockBatches) {
					for (LockToRefresh lock : lockBatch) {
						ps.setTimestamp(1, expiresTs);
						ps.setString(2, lock.id());
						ps.setTimestamp(3, lock.created());
						ps.addBatch();
					}
					// TODO once LockStats is implemented, use the update counts (index-aligned with lockBatch) to detect lost leases early:
					// a count of exactly 0 for an entry that is still registered in s_locks (recheck after the batch, as it may have been unlocked
					// since our snapshot) means its lease was lost while held - warn + report via LockStats. Note that some drivers return
					// Statement.SUCCESS_NO_INFO (-2) instead of real counts - only an exact 0 is a signal.
					ps.executeBatch();
				}
			});

		});
	}

}
