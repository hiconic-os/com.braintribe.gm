package com.braintribe.gm.initializer.jdbc.processing;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.braintribe.cfg.Required;
import com.braintribe.exception.Exceptions;
import com.braintribe.gm.initializer.api.InitializerFingerprintResolver;
import com.braintribe.gm.initializer.api.InitializerRegistry;
import com.braintribe.gm.initializer.api.InitializerState;
import com.braintribe.gm.initializer.api.InitializerStatus;
import com.braintribe.gm.initializer.api.InitializerTask;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.logging.Logger;
import com.braintribe.logging.Logger.LogLevel;
import com.braintribe.utils.FileTools;

/**
 * A file-system based implementation of {@link InitializerRegistry} that persists fingerprints in a {@link Properties} file.
 * <p>
 * The key of a fingerprint is the task name. The {@link #statusesOf(Collection) status} of the last run is stored beside it, under the task name
 * with the suffixes {@value #STATE_SUFFIX}, {@value #UPDATED_SUFFIX} and {@value #NOTE_SUFFIX}.
 */
public class FileSystemInitializerManager extends AbstractInitializerManager {

	private static final Logger log = Logger.getLogger(FileSystemInitializerManager.class);

	private static final String STATE_SUFFIX = "@state";
	private static final String UPDATED_SUFFIX = "@updated";
	private static final String NOTE_SUFFIX = "@note";

	private File storageFile;

	/** Properties file where the manager keeps track of tasks and their fingerprints. */
	@Required
	public void setTasksPropertiesFile(File storageFile) {
		this.storageFile = storageFile;
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

		private final Properties fingerprints = loadFingerprints();

		private String taskName;
		private InitializerTask task;
		private InitializerFingerprintResolver fingerprintResolver;

		private String oldFingerprint;
		private String newFingerprint;

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
			oldFingerprint = fingerprints.getProperty(taskName);
			newFingerprint = fingerprintResolver.resolveFingerprint(oldFingerprint);

			if (newFingerprint.equals(oldFingerprint)) {
				log(LogLevel.INFO, "Skipping [" + taskName + "], already up to date with fingerprint: " + newFingerprint);
				return;
			}

			runTask();
		}

		private void runTask() {
			log(LogLevel.INFO, "Running [" + taskName + "] with fingerprint: " + newFingerprint);

			try {
				Maybe<String> taskResult = task.run();

				if (taskResult.isSatisfied()) {
					fingerprints.setProperty(taskName, newFingerprint);
					String note = taskResult.get();
					writeStatus(InitializerState.SUCCEEDED, note == null ? "Success" : "Success: " + note);
					log(LogLevel.INFO, "Successfully ran [" + taskName + "] with fingerprint: " + newFingerprint);
				} else {
					String reasonMsg = taskResult.whyUnsatisfied().stringify();
					writeStatus(InitializerState.FAILED, "Failed with reason:" + reasonMsg);
					log(LogLevel.ERROR, "Task [" + taskName + "] failed with reason: " + reasonMsg);
				}

			} catch (Exception e) {
				log.error(logMsg("Error while running initializer task: " + taskName), e);
				writeStatus(InitializerState.FAILED, "Exception: " + Exceptions.stringify(e));
			}
		}

		private void writeStatus(InitializerState state, String note) {
			fingerprints.setProperty(taskName + STATE_SUFFIX, state.name());
			fingerprints.setProperty(taskName + UPDATED_SUFFIX, String.valueOf(System.currentTimeMillis()));
			fingerprints.setProperty(taskName + NOTE_SUFFIX, note);
			saveFingerprints(fingerprints);
		}
	}

	// #################################################
	// ## . . . . . . . . . Statuses . . . . . . . . .##
	// #################################################

	@Override
	protected Map<String, InitializerStatus> persistedStatusesOf(List<String> taskNames) {
		Properties props = loadFingerprints();

		Map<String, InitializerStatus> result = new LinkedHashMap<>();
		for (String taskName : taskNames)
			result.put(taskName, statusOf(taskName, props));

		return result;
	}

	/** A file written before the status was stored has only the fingerprint, which exists only after a successful run. */
	private static InitializerStatus statusOf(String taskName, Properties props) {
		String fingerprint = props.getProperty(taskName);
		String state = props.getProperty(taskName + STATE_SUFFIX);

		if (state == null)
			return fingerprint == null ? InitializerStatus.neverRun(taskName)
					: new InitializerStatus(taskName, InitializerState.SUCCEEDED, fingerprint, null, null, null);

		String updated = props.getProperty(taskName + UPDATED_SUFFIX);

		return new InitializerStatus(taskName, InitializerState.valueOf(state), fingerprint, updated == null ? null : new Date(Long.parseLong(updated)),
				null, props.getProperty(taskName + NOTE_SUFFIX));
	}

	private Properties loadFingerprints() {
		if (!storageFile.exists())
			return new Properties();

		Properties props = new Properties();
		FileTools.read(storageFile).consumeInputStream(props::load);
		return props;
	}

	private void saveFingerprints(Properties props) {
		storageFile.getParentFile().mkdirs();
		FileTools.write(storageFile).usingOutputStream(out -> props.store(out, "Initializer fingerprints for use case: " + useCase));
	}

}
