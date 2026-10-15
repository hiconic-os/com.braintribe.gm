package com.braintribe.gm.initializer.jdbc.processing;

import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static com.braintribe.utils.lcd.NullSafe.nonNull;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import javax.sql.DataSource;

import com.braintribe.cfg.Required;
import com.braintribe.exception.Exceptions;
import com.braintribe.gm.initializer.api.InitializerFingerprintResolver;
import com.braintribe.gm.initializer.api.InitializerState;
import com.braintribe.gm.initializer.api.InitializerStatus;
import com.braintribe.gm.initializer.api.InitializerTask;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmDb;
import com.braintribe.gm.jdbc.api.GmIndex;
import com.braintribe.gm.jdbc.api.GmRow;
import com.braintribe.gm.jdbc.api.GmTable;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.logging.Logger;
import com.braintribe.logging.Logger.LogLevel;
import com.braintribe.model.processing.lock.api.Locking;
import com.braintribe.utils.lcd.Lazy;

/**
 * @author peter.gazdik
 */
public class GmDbInitializerManager extends AbstractInitializerManager {

	private static final Logger log = Logger.getLogger(GmDbInitializerManager.TaskEntry.class);

	private String nodeId;
	private DataSource dataSource;
	private String tableName;
	private Locking locking;

	private final Lazy<GmDb> gmDbLazy = new Lazy<>(this::newGmDb);
	private final Lazy<TasksTable> tasksTableLazy = new Lazy<GmDbInitializerManager.TasksTable>(TasksTable::new);

	// @formatter:off
	/** Node id to be inserted to the DB table as updatedBy. */
	@Required public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	/**
	 * Configures the data source without accessing it. Initializer managers are created while modules register their initializer tasks, which may
	 * happen before a deployed data source is available. The connection and dialect are therefore resolved lazily when initializers actually run.
	 */
	@Required public void setDataSource(DataSource dataSource) { this.dataSource = nonNull(dataSource, "dataSource"); }
	@Required public void setTasksTableName(String tableName) { this.tableName = nonNull(tableName, "tableName"); }
	@Required public void setLocking(Locking locking) { this.locking = nonNull(locking, "locking"); }
	// @formatter:on

	private GmDb newGmDb() {
		return GmDb.newDb(dataSource).done();
	}

	// #################################################
	// ## . . . . . . . Initialization . . . . . . . .##
	// #################################################

	@Override
	public void runInitializers() {
		if (tasks.isEmpty())
			return;

		new InitializationRun().run();
	}

	class InitializationRun {

		private final TasksTable table = tasksTableLazy.get();

		private String taskName;
		private InitializerTask task;
		private InitializerFingerprintResolver fingerprintResolver;

		private Long taskId;
		private String oldFingerprint;
		private String newFingerprint;
		private Maybe<String> taskResult;

		public void run() {
			List<TaskEntry> tasksDepsFirst = sortTasks();

			for (TaskEntry taskEntry : tasksDepsFirst) {
				taskName = taskEntry.name();
				task = taskEntry.task();
				fingerprintResolver = taskEntry.fingerprintResolver();

				runTaskIfNeeded();
			}
		}

		private void runTaskIfNeeded() {
			queryFingerprint();

			newFingerprint = fingerprintResolver.resolveFingerprint(oldFingerprint);

			if (newFingerprint.equals(oldFingerprint)) {
				log(LogLevel.INFO, "Skipping [" + taskName + "], already up to date with fingerprint: " + newFingerprint);
				return;
			}

			Lock wLock = locking.forIdentifier("init:" + useCase + "/" + taskName).writeLock();
			wLock.lock();
			try {
				queryFingerprint();

				if (newFingerprint.equals(oldFingerprint)) {
					log(LogLevel.INFO, "Skipping [" + taskName + "], already up to date with fingerprint: " + newFingerprint);
					return;
				}

				wl_runTask();

			} finally {
				wLock.unlock();
			}
		}

		private void queryFingerprint() {
			List<GmRow> rows = table.queryFingerprint(taskName);

			if (rows.isEmpty()) {
				taskId = null;
				oldFingerprint = null;

			} else {
				GmRow row = rows.get(0);
				taskId = row.getValue(table.colId);
				oldFingerprint = row.getValue(table.colFingerprint);
			}
		}

		private void wl_runTask() {
			log(LogLevel.INFO, "Running [" + taskName + "] with fingerprint: " + newFingerprint);

			try {
				taskResult = task.run();

				if (taskResult.isSatisfied()) {
					writeSuccess(taskResult.get());

				} else {
					String reasonMsg = taskResult.whyUnsatisfied().stringify();

					log.error(logMsg("Initializer task [" + taskName + "] failed with reason:\n" + reasonMsg));
					writeError("Failed with reason:" + reasonMsg);
				}

			} catch (Exception e) {
				log.error(logMsg("Error while running initializer task: " + taskName), e);
				writeError("Exception: " + Exceptions.stringify(e));
			}
		}

		private void writeSuccess(String note) {
			note = note == null ? "Success" : "Success: " + note;
			table.write(taskId, taskName,
					asMap(table.colFingerprint, newFingerprint, table.colNote, note, table.colState, InitializerState.SUCCEEDED.name()));
		}

		private void writeError(String error) {
			table.write(taskId, taskName, asMap(table.colNote, error, table.colState, InitializerState.FAILED.name()));
		}

	}

	// #################################################
	// ## . . . . . . . . . Statuses . . . . . . . . .##
	// #################################################

	@Override
	protected Map<String, InitializerStatus> persistedStatusesOf(List<String> taskNames) {
		TasksTable table = tasksTableLazy.get();

		Map<String, InitializerStatus> result = new LinkedHashMap<>();
		for (String taskName : taskNames)
			result.put(taskName, table.queryStatus(taskName));

		return result;
	}

	private class TasksTable {
		private final GmDb gmDb = gmDbLazy.get();

		private final GmColumn<Long> colId = gmDb.autoIncrementPrimaryKeyCol("id");
		private final GmColumn<Date> colCreated = gmDb.date("created").notNull().done();
		private final GmColumn<Date> colUpdated = gmDb.date("updated").notNull().done();
		private final GmColumn<String> colUpdatedBy = gmDb.shortString255("updatedBy").notNull().done();
		private final GmColumn<String> colTaskName = gmDb.shortString255("task").notNull().done();
		private final GmColumn<String> colFingerprint = gmDb.string("fingerprint").done();
		private final GmColumn<String> colNote = gmDb.string("note").done();
		/** {@link InitializerState#SUCCEEDED} or {@link InitializerState#FAILED}. Null for a row written before this column existed. */
		private final GmColumn<String> colState = gmDb.shortString255("state").done();

		public final GmTable table;

		public TasksTable() {
			GmIndex idxTaskName = gmDb.index("idx_task_" + tableName, colTaskName);

			table = gmDb.newTable(tableName) //
					.withColumns(colId, colCreated, colUpdated, colUpdatedBy, colTaskName, colFingerprint, colNote, colState) //
					.withIndices(idxTaskName) //
					.done();

			table.ensure();
		}

		public List<GmRow> queryFingerprint(String taskName) {
			return table.select(colId, colFingerprint) //
					.whereColumn(colTaskName, taskName) //
					.rows();
		}

		public InitializerStatus queryStatus(String taskName) {
			List<GmRow> rows = table.select(colFingerprint, colUpdated, colUpdatedBy, colNote, colState) //
					.whereColumn(colTaskName, taskName) //
					.rows();

			if (rows.isEmpty())
				return InitializerStatus.neverRun(taskName);

			GmRow row = rows.get(0);
			String note = row.getValue(colNote);

			return new InitializerStatus(taskName, resolveState(row.getValue(colState), note), row.getValue(colFingerprint),
					row.getValue(colUpdated), row.getValue(colUpdatedBy), note);
		}

		/** A row written before the state column existed only has the note, which starts with "Success" for a successful run. */
		private InitializerState resolveState(String state, String note) {
			if (state != null)
				return InitializerState.valueOf(state);

			return note != null && note.startsWith("Success") ? InitializerState.SUCCEEDED : InitializerState.FAILED;
		}

		public void write(Long taskId, String taskName, Map<GmColumn<?>, Object> values) {
			Date now = new Date();

			values.put(colUpdated, now);
			values.put(colUpdatedBy, nodeId);

			if (taskId != null) {
				table.update(values).whereColumn(colId, taskId);

			} else {
				values.put(colCreated, now);
				values.put(colTaskName, taskName);
				table.insert(values);
			}
		}

	}

}
