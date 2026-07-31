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

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.lang.StackWalker.StackFrame;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.sql.DataSource;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.LifecycleAware;
import com.braintribe.cfg.Required;
import com.braintribe.exception.Exceptions;
import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Topic;
import com.braintribe.model.processing.lock.api.Locking;
import com.braintribe.model.processing.lock.api.ReentrableLocking;
import com.braintribe.model.processing.lock.api.ReentrableReadWriteLock;
import com.braintribe.provider.Box;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.util.jdbc.dialect.JdbcDialect;
import com.braintribe.utils.DigestGenerator;
import com.braintribe.utils.lcd.Lazy;
import com.braintribe.utils.lcd.NullSafe;

/**
 * {@link Locking} implementation backed by an SQL database.
 * <p>
 * It uses a table called {@value #DB_TABLE_NAME}
 * <p>
 * For each {@link Lock}, acquired e.g. via {@link #forIdentifier(String)} (or similar methods), an entry is created with a certain expiration date.
 * This expiration date is the current time plus the configured {@link #setLockExpirationInSecs(int)}.
 * 
 * <h3>Updating expiration dates automatically</h3>
 * 
 * <b>IMPORTANT:</b> This needs to be configured externally!
 * <p>
 * This expiration date should be updated automatically, ideally as a scheduled task. It should be configured externally, and the update is performed
 * by calling {@link #refreshLockedLocks()}.
 * <p>
 * Should a node fail to update the expiration date, another node will consider such entry as stale and will try to acquire the lock again.
 * <p>
 * For this reason it is advised to configure the refreshing interval significantly smaller than the lock expiration, for example one half of it.
 * 
 * <h3>Fairness</h3>
 * 
 * This class does not support reader/writer fairness.
 * 
 * <h3>Thread ownership</h3>
 *
 * Like with {@link ReentrantReadWriteLock}, a lock must be released by the same thread that acquired it. Each acquisition remembers (per thread)
 * which incarnation of the lock row it acquired, so that a lock-owner whose lease was lost (lock expired and was cleaned up or taken over by another
 * node) cannot accidentally release a newer lock of another owner.
 * <p>
 * {@code unlock()} by a thread that holds no lock throws an {@link IllegalMonitorStateException}, re-entering a lock whose lease was lost throws an
 * {@link IllegalStateException}.
 */
public class DbLocking implements Locking, LifecycleAware {

	private static final Logger log = Logger.getLogger(DbLocking.class);

	public static final String DB_TABLE_NAME = "hc_locking";

	public static final int DEFAULT_POLL_INTERVAL_MS = 100;
	public static final int DEFAULT_MAX_POLL_INTERVAL_MS = 5000;
	public static final int DEFAULT_LOCK_EXPIRATION_MS = 5 * 60 * 1000;

	private static final long NANOS_PER_MS = 1_000_000L;

	// the longer current lock has been held, the less frequently we poll: interval = lock age / this ratio (within min/max limits).
	private static final long LOCK_AGE_TO_POLL_INTERVAL_RATIO = 10;

	/* package */ DataSource dataSource;
	/* package */ JdbcDialect dialect;

	private String nodeId = "machine";

	private Supplier<MessagingSession> messagingSessionProvider;

	private long pollIntervalInNanos = DEFAULT_POLL_INTERVAL_MS * NANOS_PER_MS;
	private long maxPollIntervalInNanos = DEFAULT_MAX_POLL_INTERVAL_MS * NANOS_PER_MS;
	/* package */ long lockExpirationInMs = DEFAULT_LOCK_EXPIRATION_MS;

	private int[] unlockRetryDelaysMs = { 500, 1000, 5000 };

	private boolean autoUpdateSchema = true;

	private String topicName = "hc-locking";
	private long topicExpiration = 5000L;

	private final DbLockRefresher refresher = new DbLockRefresher(this);

	private final Lazy<DbLockingMsg> lazyMsg = new Lazy<>(DbLockingMsg::new);

	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.dialect = JdbcDialect.detectDialect(dataSource);
	}

	// @formatter:off
	@Configurable public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	@Configurable public void setAutoUpdateSchema(boolean autoUpdateSchema) { this.autoUpdateSchema = autoUpdateSchema; }

	/**
	 * Determines the minimum re-try interval to acquire a lock in case the first try wasn't successful. The actual interval grows with the age of
	 * the currently held lock (see {@link #setMaxPollIntervalInMillies(int)}) and is jittered to avoid waiters polling in lockstep.
	 * <p>
	 * Default value is {@value #DEFAULT_POLL_INTERVAL_MS}
	 */
	@Configurable public void setPollIntervalInMillies(int pollIntervalInMillies) { this.pollIntervalInNanos = pollIntervalInMillies * NANOS_PER_MS; }

	/**
	 * Upper limit for the re-try interval: the longer the current lock has already been held, the less likely it is to be released in the very next
	 * moment, so waiters poll less frequently (lock age / {@value #LOCK_AGE_TO_POLL_INTERVAL_RATIO}), but never less frequently than this limit.
	 * <p>
	 * Default value is {@value #DEFAULT_MAX_POLL_INTERVAL_MS}
	 */
	@Configurable public void setMaxPollIntervalInMillies(int maxPollIntervalInMillies) { this.maxPollIntervalInNanos = maxPollIntervalInMillies * NANOS_PER_MS; }

	@Configurable public void setLockExpirationInSecs(int lockExpirationInSecs) { this.lockExpirationInMs = 1000L * lockExpirationInSecs; }

	/**
	 * Delays (in milliseconds) between retries of the DB update that releases a lock, should it fail with an error (e.g. connection loss). One retry
	 * is attempted per array entry. Without a successful release the lock stays blocked for everyone until its lease expires, hence the retries.
	 * <p>
	 * Default value is {500, 1000, 5000}
	 */
	@Configurable public void setUnlockRetryDelaysMs(int[] unlockRetryDelaysMs) { this.unlockRetryDelaysMs = NullSafe.nonNull(unlockRetryDelaysMs, "unlockRetryDelaysMs"); }

	@Configurable public void setTopicExpiration(long topicExpiration) { this.topicExpiration = topicExpiration; }
	@Configurable public void setTopicName(String topicName) { this.topicName = topicName; }
	@Configurable public void setMessagingSessionProvider(Supplier<MessagingSession> messagingSessionProvider) { this.messagingSessionProvider = messagingSessionProvider; }
	// @formatter:on

	@Override
	public void postConstruct() {
		if (autoUpdateSchema)
			try {
				ensureLocksTable();

				Thread.ofVirtual().start(this::removeObsoleteIndices);

			} catch (Exception e) {
				throw new RuntimeException("Error while ensuring " + DB_TABLE_NAME + " table used by Locking", e);
			}
	}

	private void ensureLocksTable() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			if (!locksTableExists(connection))
				createLocksTable(connection);
		}
	}

	// 28.7.2026: we used to create these, they serve no purpose, let's make sure to remove them to improve efficiency
	private void removeObsoleteIndices() {
		try (Connection connection = dataSource.getConnection()) {
			log.debug(() -> "Dropping indices on " + DB_TABLE_NAME);

			List<String> indexNames = Stream.of("reentranceId", "count", "expires", "created") //
					.map(col -> (DB_TABLE_NAME + "_" + col + "_idx").toLowerCase()) //
					.toList();

			Set<String> existingInstances = JdbcTools.indicesExist(connection, DB_TABLE_NAME, indexNames);
			if (existingInstances.isEmpty())
				return;

			try (Statement statement = connection.createStatement()) {
				for (String indexName : indexNames) {
					String sql = "DROP INDEX IF EXISTS " + indexName + ";";
					log.info("Deleting index with statement: " + sql);
					statement.executeUpdate(sql);
					log.info("Successfully deleted index " + indexName + " in table " + DB_TABLE_NAME);
				}

			} catch (Exception e) {
				log.warn("Error while trying to drop indices: " + e.getMessage(), e);
			}
		} catch (Exception e) {
			log.warn("Error while connecting to the DB to drop indices: " + e.getMessage(), e);
		}
	}

	private void createLocksTable(Connection connection) throws SQLException {
		log.debug(() -> "Creating table " + DB_TABLE_NAME + " as it doesn't exist yet.");

		String sql = createTableSql();

		try (Statement statement = connection.createStatement()) {
			log.debug(() -> "Creating table with statement: " + sql);
			statement.executeUpdate(sql);
			log.debug(() -> "Successfully created table " + DB_TABLE_NAME);

		} catch (SQLException e) {
			if (!locksTableExists(connection))
				throw e;
		}
	}

	// TABLE

	private String createTableSql() {
		JdbcDialect jdbcDialect = JdbcDialect.detectDialect(dataSource);
		String dateType = jdbcDialect.timestampType();
		String intType = jdbcDialect.intType();

		return "create table " + DB_TABLE_NAME + " (" + //
				"id varchar(255) primary key not null, " + //
				"reentranceId varchar(255) not null, " + //
				"count " + intType + " not null, " + //
				"expires " + dateType + " not null, " + //
				"created " + dateType + " not null, " + //
				"caller varchar(255), " + //
				"machine varchar(255))";
	}

	private boolean locksTableExists(Connection connection) {
		return JdbcTools.tableExists(connection, DB_TABLE_NAME) != null;
	}

	@Override
	public void preDestroy() {
		lazyMsg.close();
	}

	@Override
	public ReentrableLocking withReentranceId(String reentranceId) {
		NullSafe.nonNull(reentranceId, "reentranceId");
		if (READ_LOCK_REENTRANCE_ID.equals(reentranceId))
			throw new IllegalArgumentException("reentranceId cannot be " + READ_LOCK_REENTRANCE_ID);

		return new ReentrableLocking() {
			@Override
			public ReentrableReadWriteLock forIdentifier(String id) {
				return forIdentifierAndReentranceId(id, reentranceId);
			}
		};
	}

	@Override
	public ReentrableReadWriteLock forIdentifier(String id) {
		return forIdentifierAndReentranceId(id, UUID.randomUUID().toString());
	}

	private ReentrableReadWriteLock forIdentifierAndReentranceId(String id, String reentranceId) {
		String truncId = truncateTo240Chars(id);
		String caller = identifyCaller();
		return new DbRwLock(truncId, reentranceId, caller);
	}

	private static final StackWalker WALKER = StackWalker.getInstance(Set.of(StackWalker.Option.RETAIN_CLASS_REFERENCE), 8);

	private String identifyCaller() {
		return WALKER.walk(framesStream -> framesStream //
				.dropWhile(f -> isFrameworkFrame(f)) //
				.findFirst() //
				.map(DbLocking::printCaller) //
				.orElse("unknown") //
		);
	}

	/**
	 * Stacktrace might have many different forms, but with no proxy the earliest possible caller position is 3.
	 * 
	 * {@code
		 StackTraceElement[0]: Thread.getStackTrace()
		 StackTraceElement[1]: this.identifyCaller()
		 StackTraceElement[2]: this.forIdentifier(String)
		 StackTraceElement[?]: ?Locking.forIdentifier(String, String)
		 StackTraceElement[?]: ?Locking.forEntity(GenericEntity)
		 StackTraceElement[?]: ?tribefire.proxy.deploy.Locking.forEntity();
		 StackTraceElement[x]: >> Caller <<
	 * }
	 */
	private static boolean isFrameworkFrame(StackFrame frame) {
		// Old way using class names, not sure it's needed,
		// String className = frame.getClassName();
		// return className.startsWith(DbLocking.class.getName()) || //
		// className.startsWith(Locking.class.getName()) || //
		// className.startsWith("tribefire.proxy.deploy.Locking");

		Class<?> c = frame.getDeclaringClass();

		return Locking.class.isAssignableFrom(c) || // covers the deploy proxy
				ReentrableLocking.class.isAssignableFrom(c) || // covers
				c == DbLocking.class || //
				c.getName().startsWith("tribefire.proxy.deploy.Locking");
	}

	private static String printCaller(StackFrame f) {
		String className = f.getClassName();
		int i = className.lastIndexOf('.');
		if (i > 0)
			className = className.substring(i + 1);

		int lineNumber = f.getLineNumber();
		String methodName = f.getMethodName();

		String caller = className + '.' + methodName + ':' + (lineNumber >= 0 ? lineNumber : "-");
		if (caller.length() <= 240)
			return caller;

		int len = caller.length();
		return caller.substring(len - 240, len);
	}

	/* package */ class DbRwLock implements ReentrableReadWriteLock {
		public final String id;
		public final String caller;

		public final DistributedLock readLock;
		public final DistributedLock writeLock;

		public DbRwLock(String id, String reentranceId, String caller) {
			this.id = id;
			this.caller = caller;

			this.readLock = new DistributedLock(this, Locking.READ_LOCK_REENTRANCE_ID, false);
			this.writeLock = new DistributedLock(this, reentranceId, true);
		}

		// @formatter:off
		@Override public String lockId() { return id; }
		@Override public String reentranceId() { return writeLock.reentranceId; }
		@Override public Lock readLock()  { return readLock; }
		@Override public Lock writeLock() { return writeLock; }
		// @formatter:on
	}

	private String truncateTo240Chars(String id) {
		NullSafe.nonNull(id, "id");
		if (id.length() <= 240)
			return id;

		try {
			String md5 = DigestGenerator.stringDigestAsString(id, "MD5");
			return id.substring(0, 200) + "#" + md5;

		} catch (Exception e) {
			throw Exceptions.unchecked(e, "Could not generate MD5 of lock id " + id);
		}
	}

	/**
	 * This method should be called periodically to refresh the locks.
	 */
	public void refreshLockedLocks() {
		refresher.refreshLockedLocks();
	}

	// ##############################################
	// ## . . . . . . . . . Lock . . . . . . . . . ##
	// ##############################################

	/**
	 * Tracks one thread's hold on a {@link DistributedLock}: which incarnation of the lock row it acquired - a timestamp and how many times it
	 * (re-)entered the lock.
	 */
	private static class LockHoldInfo {
		final Timestamp created;
		int count = 1;

		LockHoldInfo(Timestamp created) {
			this.created = created;
		}
	}

	/** Snapshot of the lock row as observed by a waiter while trying to acquire the lock. */
	private record LockRow(String reentranceId, Timestamp expires, Timestamp created) {
		// empty
	}

	private class DistributedLock implements Lock {
		private final DbRwLock rwLock;
		private final String reentranceId;
		private final boolean isWriteLock;
		private volatile boolean isWriteLocking; // to make sure writeLock is not re-entrant

		/**
		 * This thread's hold on this lock, null if it doesn't hold it. Each acquisition remembers the {@code created} timestamp of the row
		 * incarnation it acquired, and unlock only releases that exact incarnation. This implies lock and unlock must happen on the same thread, just
		 * like with {@link java.util.concurrent.locks.ReentrantReadWriteLock}.
		 */
		private final ThreadLocal<LockHoldInfo> tlLockHoldInfo = new ThreadLocal<>();

		public DistributedLock(DbRwLock rwLock, String reentranceId, boolean isWriteLock) {
			this.rwLock = rwLock;
			this.reentranceId = reentranceId;
			this.isWriteLock = isWriteLock;
		}

		private String lockContext() {
			return " id " + rwLock.id + ", reentranceId " + reentranceId;
		}

		@Override
		public void lock() {
			// per Lock.lock() contract this method is not interruptible - keep waiting and restore the interrupt status at the end
			boolean interrupted = false;
			try {
				while (true) {
					try {
						if (tryLockNanos(Long.MAX_VALUE))
							return;
					} catch (InterruptedException e) {
						log.debug("Non interruptible lock() call internally caught an InterruptedException, will continue waiting.");
						interrupted = true;
					}
				}
			} finally {
				if (interrupted)
					Thread.currentThread().interrupt();
			}
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			if (Thread.interrupted())
				throw new InterruptedException();

			if (tryLockNanos(Long.MAX_VALUE))
				return;
		}

		@Override
		public boolean tryLock() {
			try {
				return tryLockNanos(0);
			} catch (InterruptedException e) {
				// Not reachable with 0 timeout (we never reach the waiting), but just in case
				Thread.currentThread().interrupt();
				return false;
			}
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			// toNanos saturates at Long.MAX_VALUE on overflow, which is fine - that means "forever" (~292 years) below
			return tryLockNanos(unit.toNanos(time));
		}

		private boolean tryLockNanos(long tryNanos) throws InterruptedException {
			// The remaining time is counted down using the monotonic System.nanoTime(), so wall-clock adjustments (NTP etc.) can neither shorten
			// nor extend the wait. We track it as a count-down rather than a deadline, because nanoTime values have an arbitrary origin and only
			// their differences are meaningful. Long.MAX_VALUE (~292 years) effectively means "wait forever".
			long remainingNanos = tryNanos;
			long lastNanoTime = System.nanoTime();

			Thread currentThread = Thread.currentThread();
			String oldThreadName = currentThread.getName();
			currentThread.setName(oldThreadName + " > waiting for lock " + rwLock.id);
			boolean hasWriteLocking = false;

			// On first attempt, and upon unlock notification, be optimistic and insert right away
			// after failed insert, do a select to assess the situation
			boolean tryInsertFirst = true;

			try {
				while (true) {
					if (!hasWriteLocking)
						hasWriteLocking = acquireWriteLocking();

					if (hasWriteLocking) {
						LockHoldInfo lockHoldInfo = tlLockHoldInfo.get();
						if (lockHoldInfo != null)
							return reEnter(lockHoldInfo);
					}

					// register before DB attempt so an unlock notification is not lost
					CountDownLatch unlockLatch = registerUnlockWaiter(rwLock.id);
					try {
						var observedRow = new Box<LockRow>();

						if (hasWriteLocking) {
							boolean insertFirst = tryInsertFirst;
							var acquired = new Box<Boolean>();
							JdbcTools.withConnection(dataSource, true, () -> "Trying to acquire lock " + rwLock.id, connection -> {
								acquired.value = tryAcquire(connection, insertFirst, observedRow);
							});

							if (Boolean.TRUE.equals(acquired.value)) {
								refresher.startRefreshing(rwLock, tlLockHoldInfo.get().created);
								return true;
							}
						}

						long now = System.nanoTime();
						remainingNanos -= now - lastNanoTime;
						lastNanoTime = now;

						if (remainingNanos <= 0) {
							if (hasWriteLocking)
								releaseWriteLocking();
							return false;
						}

						long waitNanos = Math.min(remainingNanos, nextPollIntervalNanos(observedRow.value));
						// an unlock notification that arrived since registering still wakes us up - a counted-down latch doesn't wait at all
						tryInsertFirst = unlockLatch.await(waitNanos, TimeUnit.NANOSECONDS);

					} finally {
						unregisterUnlockWaiter(rwLock.id, unlockLatch);
					}
				}

			} catch (InterruptedException e) {
				if (hasWriteLocking)
					releaseWriteLocking();
				throw e;

			} catch (IllegalStateException e) {
				// lost lease detected while re-entering - propagate as is
				if (hasWriteLocking)
					releaseWriteLocking();
				throw e;

			} catch (Exception e) {
				if (hasWriteLocking)
					releaseWriteLocking();
				throw new RuntimeException("Could not get lock.", e);

			} finally {
				currentThread.setName(oldThreadName);
			}
		}

		/** This thread already holds the lock, so the only legitimate outcome is incrementing the count of our own row incarnation. */
		private boolean reEnter(LockHoldInfo hold) {
			var successIndicator = new Box<Boolean>();
			JdbcTools.withConnection(dataSource, true, () -> "Re-entering lock " + rwLock.id, connection -> {
				successIndicator.value = tryChangeCount(connection, hold.created, +1);
			});

			if (!Boolean.TRUE.equals(successIndicator.value))
				throw new IllegalStateException("Cannot re-enter lock, even though it's already held by current thread. "
						+ "Its lease was lost - the lock expired, probably due to some error with keeping it alive: " + lockContext());

			hold.count++;
			return true;
		}

		/**
		 * Single acquisition attempt. If insertFirst, optimistically tries an insert (the row probably doesn't exist); otherwise it starts with a
		 * select of the current row state and only attempts the write that state calls for - this way a failed attempt of a blocked waiter costs just
		 * one cheap single-row select.
		 * <p>
		 * If not acquired, the observed row (null if there was none) is stored in observedRow and drives the length of the subsequent wait.
		 */
		private boolean tryAcquire(Connection c, boolean insertFirst, Box<LockRow> observedRow) throws Exception {
			if (insertFirst && tryInsert(c))
				return true;

			LockRow row = queryLockRow(c);
			observedRow.value = row;

			if (row == null)
				// no row (or it disappeared since our failed insert) - grab it
				return tryInsert(c);

			if (reentranceId.equals(row.reentranceId())) {
				// a lock with our reentranceId exists (e.g. another read lock) - join it by incrementing its count
				if (tryChangeCount(c, row.created(), +1)) {
					tlLockHoldInfo.set(new LockHoldInfo(row.created()));
					return true;
				}
				// the row changed between our select and update - try again next round
				return false;
			}

			if (isExpired(row) && deleteLockIfExpired(c))
				return tryInsert(c);

			return false;
		}

		private boolean isExpired(LockRow row) {
			return row.expires().getTime() <= System.currentTimeMillis();
		}

		private LockRow queryLockRow(Connection c) {
			String query = "select reentranceId, expires, created from " + DB_TABLE_NAME + " where id = ?";

			var result = new Box<LockRow>();
			JdbcTools.withPreparedStatement(c, query, () -> "Querying lock row with id " + rwLock.id, ps -> {
				ps.setString(1, rwLock.id);

				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next())
						result.value = new LockRow(rs.getString(1), rs.getTimestamp(2), rs.getTimestamp(3));
				}
			});

			return result.value;
		}

		// id, expires, created, caller, machine
		private boolean tryInsert(Connection c) {
			long current = System.currentTimeMillis();
			long expires = current + lockExpirationInMs;

			Timestamp currentTs = new Timestamp(current);
			Timestamp expiresTs = new Timestamp(expires);

			boolean result = tryInsert(c, currentTs, expiresTs);
			if (result)
				tlLockHoldInfo.set(new LockHoldInfo(currentTs));

			return result;
		}

		private boolean tryInsert(Connection c, Timestamp currentTs, Timestamp expiresTs) {
			switch (dialect.knownDbVariant()) {
				case postgre:
					return tryInsertPostgres(c, currentTs, expiresTs);
				default:
					return tryInsertStandard(c, currentTs, expiresTs);
			}
		}

		private boolean tryInsertPostgres(Connection c, Timestamp currentTs, Timestamp expiresTs) {
			String query = "insert into " + DB_TABLE_NAME
					+ " (id, reentranceId, count, expires, created, caller, machine) values (?,?,?,?,?,?,?) on conflict (id) do nothing;";
			return tryInsertWithConflictHandling(c, currentTs, expiresTs, query);
		}

		private boolean tryInsertWithConflictHandling(Connection c, Timestamp currentTs, Timestamp expiresTs, String query) {
			Box<Integer> updated = new Box<>();
			JdbcTools.withPreparedStatement(c, query, () -> "inserting entry for lock with id " + rwLock.id, ps -> {
				int i = 1;
				ps.setString(i++, rwLock.id);
				ps.setString(i++, reentranceId);
				ps.setInt(i++, 1); // count
				ps.setTimestamp(i++, expiresTs);
				ps.setTimestamp(i++, currentTs);
				ps.setString(i++, rwLock.caller);
				ps.setString(i++, nodeId);

				updated.value = ps.executeUpdate();
			});

			return updated.value > 0;
		}

		// @formatter:off
		/* For some DBs we could do an insert without duplicate key violation this ways:
		 *  	insert into hc_locking (id, reentranceId, count, expires, created, caller, machine)
		 *  		select ?, ?, ?, ?, ?, ?, ?
		 *			where not exists (select 1 from hc_locking where id = ?);
		 *
		 *  But it's a bit tricky, because it doesn't work out of the box, as the default isolation level for JDBC is READ_COMMITTED
		 *	-> hence two parallel query executions both see the row with given id doesn't exist and try to insert
		 *  -> It might work if for this query only we set the
		 *  		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		 */
		// @formatter:on

		private boolean tryInsertStandard(Connection c, Timestamp currentTs, Timestamp expiresTs) {
			String query = "insert into " + DB_TABLE_NAME + " (id, reentranceId, count, expires, created, caller, machine) values (?,?,?,?,?,?,?)";

			try {
				JdbcTools.withPreparedStatement(c, query, () -> "inserting entry for lock with id " + rwLock.id, ps -> {
					int i = 1;
					ps.setString(i++, rwLock.id);
					ps.setString(i++, reentranceId);
					ps.setInt(i++, 1); // count
					ps.setTimestamp(i++, expiresTs);
					ps.setTimestamp(i++, currentTs);
					ps.setString(i++, rwLock.caller);
					ps.setString(i++, nodeId);

					ps.executeUpdate();
				});

				return true;

			} catch (Exception e) {
				SQLException sqlE = JdbcTools.unwrapSqlException(e);
				if (sqlE != null) {
					String sqlState = sqlE.getSQLState();
					// indicates a constraint violation, e.g. duplicate key, which means a row with given lock id already exists
					boolean isIntegrityConstraintViolation = sqlState != null && sqlState.startsWith("23");
					if (isIntegrityConstraintViolation)
						return false;
				}

				throw e;
			}
		}

		// Throws wrapped SQLExceptions!!!
		private boolean tryChangeCount(Connection c, Timestamp created, int diff) {
			long current = System.currentTimeMillis();
			long expires = current + lockExpirationInMs;

			Timestamp expiresTs = new Timestamp(expires);

			String query = "update " + DB_TABLE_NAME + " set count = count + ?, expires = ? where id = ? and reentranceId = ? and created = ?";

			var updated = new Box<Integer>();
			JdbcTools.withPreparedStatement(c, query, () -> "updating count for lock with id " + rwLock.id, ps -> {
				ps.setInt(1, diff);
				ps.setTimestamp(2, expiresTs);
				ps.setString(3, rwLock.id);
				ps.setString(4, reentranceId);
				ps.setTimestamp(5, created);

				updated.value = ps.executeUpdate();
			});

			return updated.value > 0;
		}

		/**
		 * Wait time depends on how long current lock row was held - the longer, the less likely it will be released soon, within limits.
		 * <p>
		 * Result is jittered to +-50% so that concurrent waiters don't poll in lock-step.
		 * <p>
		 * But if the lock is about to expire, we wake up right expiration.
		 */
		private long nextPollIntervalNanos(LockRow row) {
			if (row == null)
				return jittered(pollIntervalInNanos);

			long now = System.currentTimeMillis();

			long lockAgeMs = now - row.created().getTime(); // may even be negative with cross-node clock skew
			long ageBasedNanos = lockAgeMs / LOCK_AGE_TO_POLL_INTERVAL_RATIO * NANOS_PER_MS;
			long baseNanos = Math.min(maxPollIntervalInNanos, Math.max(pollIntervalInNanos, ageBasedNanos));

			long jitteredNanos = jittered(baseNanos);

			// +1 ms so we wake up just after the expiration, not just before it
			long untilExpirationNanos = (row.expires().getTime() - now + 1) * NANOS_PER_MS;
			if (untilExpirationNanos > 0)
				return Math.min(jitteredNanos, untilExpirationNanos);

			return jitteredNanos;
		}

		/** Random value between 50% and 150% of nanos */
		private long jittered(long nanos) {
			return nanos / 2 + ThreadLocalRandom.current().nextLong(nanos + 1);
		}

		@Override
		public void unlock() {
			LockHoldInfo lockHoldInfo = tlLockHoldInfo.get();
			if (lockHoldInfo == null)
				throw new IllegalMonitorStateException("Attempt to unlock a lock, not locked by current thread: " + lockContext());

			if (lockHoldInfo.count == 1)
				refresher.stopRefreshing(rwLock, lockHoldInfo.created);

			try {
				boolean lockReleased = releaseLockInDb(lockHoldInfo);

				// only notify waiters if the lock row was actually deleted - as long as it exists (count > 0) they cannot acquire it anyway
				if (lockReleased)
					notifyUnlock(rwLock.id);

			} catch (Exception e) {
				log.warn("Error while releasing lock " + rwLock.id + ", it will stay blocked for everyone until its lease expires.", e);

			} finally {
				if (--lockHoldInfo.count == 0)
					tlLockHoldInfo.remove();
				if (isWriteLocking)
					releaseWriteLocking();
			}
		}

		/**
		 * Releases this thread's hold in the DB and says whether the lock row was actually deleted (i.e. waiters can now acquire the lock).
		 * <p>
		 * DB errors are retried as per {@link #setUnlockRetryDelaysMs(int[])}, to really try not to leave behind a stale lock.
		 */
		private boolean releaseLockInDb(LockHoldInfo lockHoldInfo) throws Exception {
			// Common case: we are the sole holder - delete the row outright, with a single statement.
			if (deleteLockIfSoleHolder(lockHoldInfo))
				return true;

			// Other holders exist - just decrement the count.
			if (!decrementLockCount(lockHoldInfo)) {
				// Our row incarnation is gone: the lease was lost, or (rarely) a delete retry above reached the DB but reporting its result failed.
				log.warn("Lock lease was lost while held (the lock expired and was released or taken over by another owner) - mutual "
						+ "exclusion may have been violated during the critical section." + lockContext());
				return false;
			}

			// In case a concurrent unlock brought the count down to 1 between our two statements above, our decrement left the row at 0 - delete it.
			return retryingOnDbErrors(() -> {
				var deleted = new Box<Boolean>();
				JdbcTools.withConnection(dataSource, true, () -> "Deleting released lock " + rwLock.id, connection -> {
					deleted.value = deleteLockIfExpired(connection);
				});

				return Boolean.TRUE.equals(deleted.value);
			});
		}

		private boolean deleteLockIfSoleHolder(LockHoldInfo lockHoldInfo) throws Exception {
			String query = "delete from " + DB_TABLE_NAME + " where id = ? and reentranceId = ? and created = ? and count = 1";

			return retryingOnDbErrors(() -> {
				var deleted = new Box<Integer>();
				JdbcTools.withConnection(dataSource, true, () -> "Deleting unlocked lock " + rwLock.id, connection -> {
					JdbcTools.withPreparedStatement(connection, query, () -> "deleting entry for lock with id " + rwLock.id, ps -> {
						ps.setString(1, rwLock.id);
						ps.setString(2, reentranceId);
						ps.setTimestamp(3, lockHoldInfo.created);

						deleted.value = ps.executeUpdate();
					});
				});

				return deleted.value > 0;
			});
		}

		private boolean decrementLockCount(LockHoldInfo lockHoldInfo) throws Exception {
			var decremented = new Box<Boolean>();
			JdbcTools.withConnection(dataSource, true, () -> "Decrementing count of lock " + rwLock.id, connection -> {
				decremented.value = tryChangeCount(connection, lockHoldInfo.created, -1);
			});

			return Boolean.TRUE.equals(decremented.value);
		}

		private boolean retryingOnDbErrors(Callable<Boolean> dbTask) throws Exception {
			int nextRetry = 0;

			while (true) {
				try {
					return dbTask.call();

				} catch (Exception e) {
					if (nextRetry >= unlockRetryDelaysMs.length)
						throw e;

					long delayMs = unlockRetryDelaysMs[nextRetry++];
					log.warn("Error while releasing lock " + rwLock.id + ", retrying in " + delayMs + " ms.", e);

					try {
						Thread.sleep(delayMs);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw e; // interrupted - give up retrying
					}
				}
			}
		}

		protected boolean deleteLockIfExpired(Connection c) throws Exception {
			Timestamp curentTs = new Timestamp(System.currentTimeMillis());

			var deleted = new Box<Integer>();

			String query = "delete from " + DB_TABLE_NAME + " where id = ? and (expires < ? or count = 0)";
			List<Object> params = asList(rwLock.id, curentTs);

			JdbcTools.withPreparedStatement(c, query, params, () -> "Deleting expired lock " + rwLock.id, ps -> {
				ps.setString(1, rwLock.id);
				ps.setTimestamp(2, curentTs);

				deleted.value = ps.executeUpdate();
			});

			return deleted.value > 0;
		}

		private boolean acquireWriteLocking() {
			if (!isWriteLock)
				return true;

			if (isWriteLocking)
				return false;

			synchronized (this) {
				if (isWriteLocking)
					return false;

				return isWriteLocking = true;
			}
		}

		private void releaseWriteLocking() {
			if (isWriteLock)
				isWriteLocking = false;
		}

		@Override
		public Condition newCondition() {
			throw new UnsupportedOperationException("newCondition not supported by this implementation");
		}

	}

	private void notifyUnlock(String lockId) {
		DbLockingMsg msg = lazyMsg.get();
		if (msg.msgSession != null && msg.msgProducer != null) {
			try {
				Message message = msg.msgSession.createMessage();
				message.setBody(lockId);
				message.setTimeToLive(topicExpiration);

				msg.msgProducer.sendMessage(message);

			} catch (MessagingException e) {
				log.error("error while producing message for a lock", e);
			}
		}
	}

	class DbLockingMsg implements AutoCloseable {
		private Topic topic;
		private MessageProducer msgProducer;
		/** Shared consumer for all lock waiters of this instance, dispatching unlock notifications via {@link DbLocking#onUnlockMessage(Message)}. */
		private MessageConsumer msgConsumer;
		private MessagingSession msgSession;

		public DbLockingMsg() {
			if (messagingSessionProvider == null)
				return;

			try {
				msgSession = messagingSessionProvider.get();
				topic = msgSession.createTopic(topicName);
				msgProducer = msgSession.createMessageProducer(topic);
				msgConsumer = msgSession.createMessageConsumer(topic);
				msgConsumer.setMessageListener(DbLocking.this::onUnlockMessage);

			} catch (MessagingException e) {
				log.error("error while retrieving messaging components", e);
				close();
			}
		}

		@Override
		public void close() {
			if (msgProducer != null)
				try {
					msgProducer.close();
				} catch (Exception e) {
					log.warn("error while closing message producer", e);
				} finally {
					msgProducer = null;
				}

			if (msgConsumer != null)
				try {
					msgConsumer.close();
				} catch (Exception e) {
					log.warn("error while closing message consumer", e);
				} finally {
					msgConsumer = null;
				}

			if (msgSession != null)
				try {
					msgSession.close();
				} catch (MessagingException e) {
					log.warn("error while closing messaging session", e);
				} finally {
					msgSession = null;
				}
		}
	}

	// ##############################################
	// ## . . . . Waiting for unlock . . . . . . .##
	// ##############################################

	/** Threads waiting for a lock, per lock id, to be woken up by an unlock notification. See {@link #registerUnlockWaiter(String)}. */
	private final Map<String, Set<CountDownLatch>> lockIdToWaiterCdls = new ConcurrentHashMap<>();

	/**
	 * Registers a latch that is counted down when an unlock notification for given lock id arrives via the shared {@link DbLockingMsg#msgConsumer
	 * message consumer}. The caller must {@link #unregisterUnlockWaiter(String, CountDownLatch) unregister} it in a finally block.
	 * <p>
	 * If messaging is not configured, the latch is never counted down and waiting on it degrades to pure polling.
	 */
	private CountDownLatch registerUnlockWaiter(String lockId) {
		lazyMsg.get(); // ensures the shared consumer is listening (if messaging is configured at all)

		CountDownLatch latch = new CountDownLatch(1);
		lockIdToWaiterCdls.compute(lockId, (id, latches) -> {
			Set<CountDownLatch> result = latches == null ? ConcurrentHashMap.newKeySet() : latches;
			result.add(latch);
			return result;
		});

		return latch;
	}

	private void unregisterUnlockWaiter(String lockId, CountDownLatch latch) {
		lockIdToWaiterCdls.compute(lockId, (id, latches) -> {
			if (latches == null)
				return null;

			latches.remove(latch);
			return latches.isEmpty() ? null : latches;
		});
	}

	private void onUnlockMessage(Message message) {
		Object body = message.getBody();
		if (!(body instanceof String))
			return;

		String lockId = (String) body;
		Set<CountDownLatch> latches = lockIdToWaiterCdls.get(lockId);
		if (latches != null)
			latches.forEach(CountDownLatch::countDown);
	}

}
