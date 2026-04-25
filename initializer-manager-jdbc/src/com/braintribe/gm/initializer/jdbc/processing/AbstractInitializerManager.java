package com.braintribe.gm.initializer.jdbc.processing;

import static com.braintribe.utils.lcd.CollectionTools2.newConcurrentSet;
import static com.braintribe.utils.lcd.CollectionTools2.newLinkedSet;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.cfg.Required;
import com.braintribe.gm.initializer.api.InitializerFingerprintResolver;
import com.braintribe.gm.initializer.api.InitializerRegistry;
import com.braintribe.gm.initializer.api.InitializerTask;
import com.braintribe.logging.Logger;
import com.braintribe.logging.Logger.LogLevel;

/**
 * @author peter.gazdik
 */
public abstract class AbstractInitializerManager implements InitializerRegistry {

	private static final Logger log = Logger.getLogger(AbstractInitializerManager.class);

	protected final Map<String, TaskEntry> tasks = new ConcurrentHashMap<>();
	protected final Map<String, Set<String>> taskNameToDependencyNames = new ConcurrentHashMap<>();

	record TaskEntry(String name, InitializerTask task, InitializerFingerprintResolver fingerprintResolver) {
	}

	protected String useCase;

	/** Description of the use case, such as db-schema-update or master-data-sync. */
	@Required
	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	public abstract void runInitializers();

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
	// ## . . . . . . . Task Sorting . . . . . . . . .##
	// #################################################

	protected List<TaskEntry> sortTasks() {
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

	// #################################################
	// ## . . . . . . . . Logging . . . . . . . . . . ##
	// #################################################

	protected void log(LogLevel level, String string) {
		if (log.isLevelEnabled(level))
			log.log(level, logMsg(string));
	}

	protected String logMsg(String string) {
		return "Initializer [" + useCase + "]: " + string;
	}

}
