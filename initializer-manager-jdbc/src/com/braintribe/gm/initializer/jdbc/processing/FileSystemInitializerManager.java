package com.braintribe.gm.initializer.jdbc.processing;

import java.io.File;
import java.util.List;
import java.util.Properties;

import com.braintribe.cfg.Required;
import com.braintribe.gm.initializer.api.InitializerFingerprintResolver;
import com.braintribe.gm.initializer.api.InitializerRegistry;
import com.braintribe.gm.initializer.api.InitializerTask;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.logging.Logger;
import com.braintribe.logging.Logger.LogLevel;
import com.braintribe.utils.FileTools;

/**
 * A file-system based implementation of {@link InitializerRegistry} that persists fingerprints in a {@link Properties} file.
 */
public class FileSystemInitializerManager extends AbstractInitializerManager {

	private static final Logger log = Logger.getLogger(FileSystemInitializerManager.class);

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
			try {
				Maybe<String> taskResult = task.run();

				if (taskResult.isSatisfied()) {
					fingerprints.setProperty(taskName, newFingerprint);
					saveFingerprints(fingerprints);
					log(LogLevel.INFO, "Successfully ran [" + taskName + "] with fingerprint: " + newFingerprint);
				} else {
					log(LogLevel.ERROR, "Task [" + taskName + "] failed with reason: " + taskResult.whyUnsatisfied().stringify());
				}

			} catch (Exception e) {
				log.error(logMsg("Error while running initializer task: " + taskName), e);
			}
		}
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
