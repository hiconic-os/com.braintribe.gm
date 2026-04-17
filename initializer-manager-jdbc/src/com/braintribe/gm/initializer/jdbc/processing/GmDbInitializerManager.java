package com.braintribe.gm.initializer.jdbc.processing;

import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static com.braintribe.utils.lcd.CollectionTools2.newConcurrentSet;
import static com.braintribe.utils.lcd.CollectionTools2.newLinkedSet;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import javax.sql.DataSource;

import com.braintribe.cfg.Required;
import com.braintribe.gm.initializer.api.InitializerFingerprintResolver;
import com.braintribe.gm.initializer.api.InitializerRegistry;
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
public class GmDbInitializerManager implements InitializerRegistry {

	private static final Logger log = Logger.getLogger(GmDbInitializerManager.TaskEntry.class);

	private final Map<String, TaskEntry> tasks = new ConcurrentHashMap<>();
	private final Map<String, Set<String>> taskNameToDependencyNames = new ConcurrentHashMap<>();

	record TaskEntry(String name, InitializerTask task, InitializerFingerprintResolver fingerprintResolver) {
	}

	private String useCase;
	private String nodeId;
	private GmDb gmDb;
	private String tableName;
	private Locking locking;

	private final Lazy<TasksTable> tasksTableLazy = new Lazy<GmDbInitializerManager.TasksTable>(TasksTable::new);

	// @formatter:off
	/** Description of the use case, such as db-schema-update or master-data-sync. */
	@Required public void setUseCase(String useCase) { this.useCase = useCase; }
	/** Node id to be inserted to the DB table as updatedBy. */
	@Required public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	@Required public void setDataSource(DataSource dataSource) { this.gmDb = GmDb.newDb(dataSource).done(); }
	@Required public void setTasksTableName(String tableName) { this.tableName = tableName; }
	@Required public void setLocking(Locking locking) { this.locking = locking; }
	// @formatter:on

	// #################################################
	// ## . . . . . . InitializerRegistry . . . . . . ##
	// #################################################

	@Override
	public void registerInitializer(String initializerName, InitializerFingerprintResolver fingerprintResolver, InitializerTask task) {
		tasks.put(initializerName, new TaskEntry(initializerName, task, fingerprintResolver));

	}

	@Override
	public void ensureOrder(String runsFirstName, String runsLaterNamer) {
		taskNameToDependencyNames.computeIfAbsent(runsLaterNamer, k -> newConcurrentSet()).add(runsFirstName);
	}

	// #################################################
	// ## . . . . . . . Initialization . . . . . . . .##
	// #################################################

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
				taskName = taskEntry.name;
				task = taskEntry.task;
				fingerprintResolver = taskEntry.fingerprintResolver;

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
			try {
				taskResult = task.run();

				if (taskResult.isSatisfied())
					writeSuccess(taskResult.get());
				else
					writeError("Failed with reason:" + taskResult.whyUnsatisfied().stringify());

			} catch (Exception e) {
				log.error(logMsg("Error while running initializer task: " + taskName), e);
				writeError("Exception [" + e.getClass().getSimpleName() + "]: " + e.getMessage());
			}
		}

		private void writeSuccess(String note) {
			note = note == null ? "Success" : "Success: " + note;
			table.write(taskId, taskName, asMap(table.colFingerprint, newFingerprint, table.colNote, note));
		}

		private void writeError(String error) {
			table.write(taskId, taskName, asMap(table.colNote, error));
		}

	}

	private List<TaskEntry> sortTasks() {
		Set<String> added = newSet();
		Set<String> visitTrail = newLinkedSet();
		List<TaskEntry> result = new ArrayList<>();

		for (String taskName : tasks.keySet())
			addDependencies(taskName, added, visitTrail, result);

		return result;
	}

	private void addDependencies(String taskName, Set<String> added, Set<String> visitTrail, List<TaskEntry> result) {
		if (!visitTrail.add(taskName))
			throw new IllegalStateException(
					"Cycle detected in initializer task dependencies at task [" + taskName + "]. Previous tasks: " + visitTrail);

		try {
			if (!added.add(taskName))
				// already added,
				return;

			Set<String> dependencyNames = taskNameToDependencyNames.get(taskName);
			if (dependencyNames != null)
				for (String dependencyName : dependencyNames)
					addDependencies(dependencyName, added, visitTrail, result);

			result.add(tasks.get(taskName));

		} finally {
			visitTrail.remove(taskName);
		}
	}

	private void log(LogLevel level, String string) {
		if (log.isLevelEnabled(level))
			log.log(level, logMsg(string));
	}

	private String logMsg(String string) {
		return "Initializer [" + useCase + "]: " + string;
	}

	private class TasksTable {

		private final GmColumn<Long> colId = gmDb.autoIncrementPrimaryKeyCol("id");
		private final GmColumn<Date> colCreated = gmDb.date("created").notNull().done();
		private final GmColumn<Date> colUpdated = gmDb.date("updated").notNull().done();
		private final GmColumn<String> colUpdatedBy = gmDb.shortString255("updatedBy").notNull().done();
		private final GmColumn<String> colTaskName = gmDb.shortString255("task").notNull().done();
		private final GmColumn<String> colFingerprint = gmDb.string("fingerprint").done();
		private final GmColumn<String> colNote = gmDb.string("note").done();

		public final GmTable table;

		public TasksTable() {
			GmIndex idxTaskName = gmDb.index("idx_task_" + tableName, colTaskName);

			table = gmDb.newTable(tableName) //
					.withColumns(colId, colCreated, colUpdated, colUpdatedBy, colTaskName, colFingerprint, colNote) //
					.withIndices(idxTaskName) //
					.done();

			table.ensure();
		}

		public List<GmRow> queryFingerprint(String taskName) {
			return table.select(colId, colFingerprint) //
					.whereColumn(colTaskName, taskName) //
					.rows();
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
