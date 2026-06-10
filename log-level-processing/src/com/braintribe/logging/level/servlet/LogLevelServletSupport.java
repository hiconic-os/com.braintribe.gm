package com.braintribe.logging.level.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.braintribe.gm.logging.level.LogLevelApplicationResolver;
import com.braintribe.gm.model.logging.level.api.GetLogLevelState;
import com.braintribe.gm.model.logging.level.api.LogLevelState;
import com.braintribe.gm.model.logging.level.api.UpdateRuntimeLogLevels;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.logging.level.LogLevelConfiguration.LogLevelEntry;
import com.braintribe.logging.level.StructuredPackageComparator;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Neutral;

public class LogLevelServletSupport {
	private static final String PAGE_RESOURCE = "log-levels.html";

	private static final String ACTION_SET = "set";
	private static final String ACTION_RESET_TO_DEPLOYMENT = "resetToDeployment";
	private static final String ACTION_CLEAR_PERSISTENCE = "clearPersistence";

	private Evaluator<ServiceRequest> evaluator;
	private InstanceId localInstanceId;
	private LogLevelApplicationResolver applicationResolver;

	public void setEvaluator(Evaluator<ServiceRequest> evaluator) {
		this.evaluator = evaluator;
	}

	public void setLocalInstanceId(InstanceId localInstanceId) {
		this.localInstanceId = localInstanceId;
	}

	public void setApplicationResolver(LogLevelApplicationResolver applicationResolver) {
		this.applicationResolver = applicationResolver;
	}

	public Maybe<Neutral> handlePost(String action, ParameterProvider parameters) {
		if (action == null) {
			return unsupportedAction(action);
		}

		String selectedApplication = selectedApplication(parameters.getParameter("app"));

		switch (action) {
			case ACTION_CLEAR_PERSISTENCE:
				return updateRuntime(() -> {
					UpdateRuntimeLogLevels request = UpdateRuntimeLogLevels.T.create();
					request.setClearAll(true);
					request.setApplicationId(selectedApplication);
					return request;
				});
			case ACTION_SET:
				return handleSet(parameters, selectedApplication);
			case ACTION_RESET_TO_DEPLOYMENT:
				return handleResetToDeployment(parameters, selectedApplication);
			default:
				return unsupportedAction(action);
		}
	}

	public Maybe<String> pageHtml() {
		try (InputStream in = getClass().getResourceAsStream(PAGE_RESOURCE)) {
			if (in == null) {
				return InternalError.create("Missing classpath resource: " + PAGE_RESOURCE).asMaybe();
			}

			StringBuilder html = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					html.append(line).append('\n');
				}
			}
			return Maybe.complete(html.toString());
		} catch (IOException e) {
			return InternalError.from(e, "Failed to load log level servlet page").asMaybe();
		}
	}

	public Maybe<String> stateJson(String requestedApplication) {
		String selectedApplication = selectedApplication(requestedApplication);
		Maybe<LogLevelState> maybeState = readState(selectedApplication);
		if (maybeState.isUnsatisfied()) {
			return maybeState.propagateReason();
		}

		LogLevelState state = maybeState.get();
		Map<String, LogLevelEntry> entries = entries(state);

		StringBuilder json = new StringBuilder();
		json.append('{');
		appendStringProperty(json, "selectedApplication", selectedApplication);
		json.append(',');
		appendStringArrayProperty(json, "applications", liveApplications(selectedApplication));
		json.append(',');
		appendEntries(json, entries);
		json.append(',');
		appendStringArrayProperty(json, "knownLoggerNames", state.getKnownLoggerNames());
		json.append('}');

		return Maybe.complete(json.toString());
	}

	public String selectedApplication(String requestedApplication) {
		if (!remoteAccessConfigured()) {
			return localApplicationId();
		}

		if (requestedApplication != null && !requestedApplication.trim().isEmpty()) {
			return requestedApplication.trim();
		}

		return localApplicationId();
	}

	private Maybe<Neutral> handleSet(ParameterProvider parameters, String selectedApplication) {
		Maybe<String> name = loggerNameParameter(parameters, "name");
		Maybe<String> level = parameter(parameters, "level");

		if (name.isUnsatisfied()) {
			return name.propagateReason();
		}
		if (level.isUnsatisfied()) {
			return level.propagateReason();
		}

		return updateRuntime(() -> {
				UpdateRuntimeLogLevels request = UpdateRuntimeLogLevels.T.create();
				Map<String, String> levels = new LinkedHashMap<>();
				levels.put(name.get(), level.get());
				request.setLevels(levels);
				request.setNamesToRemove(Collections.emptySet());
				request.setApplicationId(selectedApplication);
				return request;
			});
	}

	private Maybe<Neutral> handleResetToDeployment(ParameterProvider parameters, String selectedApplication) {
		Maybe<String> name = loggerNameParameter(parameters, "name");

		if (name.isUnsatisfied()) {
			return name.propagateReason();
		}

		return updateRuntime(() -> {
			UpdateRuntimeLogLevels request = UpdateRuntimeLogLevels.T.create();
			request.setLevels(Collections.emptyMap());
			Set<String> namesToRemove = new LinkedHashSet<>();
			namesToRemove.add(name.get());
			request.setNamesToRemove(namesToRemove);
			request.setApplicationId(selectedApplication);
			return request;
		});
	}

	private Maybe<Neutral> updateRuntime(RequestSupplier requestSupplier) {
		try {
			return requestSupplier.get().eval(evaluator).getReasoned();
		} catch (Exception e) {
			return InternalError.from(e, "Failed to update runtime log levels").asMaybe();
		}
	}

	private Maybe<Neutral> unsupportedAction(String action) {
		return Maybe.empty(InvalidArgument.create("Unsupported log level action: " + action));
	}

	private Maybe<LogLevelState> readState(String selectedApplication) {
		GetLogLevelState request = GetLogLevelState.T.create();
		request.setApplicationId(selectedApplication);
		return request.eval(evaluator).getReasoned();
	}

	private Map<String, LogLevelEntry> entries(LogLevelState state) {
		Map<String, LogLevelEntry> entries = new TreeMap<>(new StructuredPackageComparator());
		addNames(entries, state.getPackagedLevels());
		addNames(entries, state.getRuntimeLevels());

		for (LogLevelEntry entry: entries.values()) {
			entry.deployedLevel = value(state.getPackagedLevels(), entry.name);
			entry.persistentLevel = value(state.getRuntimeLevels(), entry.name);
			entry.effectiveLevel = value(state.getEffectiveLevels(), entry.name);
		}

		return entries;
	}

	private void addNames(Map<String, LogLevelEntry> entries, Map<String, String> levels) {
		if (levels == null) {
			return;
		}

		for (String name: levels.keySet()) {
			if (name != null) {
				entries.put(name, new LogLevelEntry(name));
			}
		}
	}

	private String value(Map<String, String> map, String name) {
		return map == null ? null : map.get(name);
	}

	private Set<String> liveApplications(String selectedApplication) {
		Set<String> applications = applicationResolver == null ? null : applicationResolver.liveApplications();
		Set<String> result = new TreeSet<>();

		if (applications != null) {
			result.addAll(applications);
		}

		String localApplication = localApplicationId();
		if (localApplication != null) {
			result.add(localApplication);
		}
		if (selectedApplication != null && !selectedApplication.isEmpty()) {
			result.add(selectedApplication);
		}

		return result;
	}

	private String localApplicationId() {
		return localInstanceId == null ? null : localInstanceId.getApplicationId();
	}

	private boolean remoteAccessConfigured() {
		return evaluator != null && applicationResolver != null;
	}

	private Maybe<String> parameter(ParameterProvider parameters, String name) {
		String value = parameters.getParameter(name);
		if (value == null || value.trim().isEmpty()) {
			return Maybe.empty(InvalidArgument.create("Missing parameter: " + name));
		}
		return Maybe.complete(value.trim());
	}

	private Maybe<String> loggerNameParameter(ParameterProvider parameters, String name) {
		String value = parameters.getParameter(name);
		if (value == null) {
			return Maybe.empty(InvalidArgument.create("Missing parameter: " + name));
		}
		return Maybe.complete(value.trim());
	}

	private void appendEntries(StringBuilder json, Map<String, LogLevelEntry> entries) {
		json.append("\"entries\":[");
		boolean first = true;
		for (LogLevelEntry entry: entries.values()) {
			if (!first) {
				json.append(',');
			}
			first = false;
			json.append('{');
			appendStringProperty(json, "name", entry.name);
			json.append(',');
			appendStringProperty(json, "effective", entry.effectiveLevel);
			json.append(',');
			appendStringProperty(json, "runtime", entry.persistentLevel);
			json.append(',');
			appendStringProperty(json, "packaged", entry.deployedLevel);
			json.append('}');
		}
		json.append(']');
	}

	private void appendStringArrayProperty(StringBuilder json, String name, Set<String> values) {
		json.append('"').append(name).append("\":[");
		boolean first = true;
		if (values != null) {
			for (String value: values) {
				if (value == null) {
					continue;
				}
				if (!first) {
					json.append(',');
				}
				first = false;
				appendJsonString(json, value);
			}
		}
		json.append(']');
	}

	private void appendStringProperty(StringBuilder json, String name, String value) {
		json.append('"').append(name).append("\":");
		appendJsonString(json, value);
	}

	private void appendJsonString(StringBuilder json, String value) {
		if (value == null) {
			json.append("null");
			return;
		}

		json.append('"');
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
				case '"':
					json.append("\\\"");
					break;
				case '\\':
					json.append("\\\\");
					break;
				case '\b':
					json.append("\\b");
					break;
				case '\f':
					json.append("\\f");
					break;
				case '\n':
					json.append("\\n");
					break;
				case '\r':
					json.append("\\r");
					break;
				case '\t':
					json.append("\\t");
					break;
				default:
					if (c < 0x20) {
						json.append(String.format("\\u%04x", (int) c));
					} else {
						json.append(c);
					}
			}
		}
		json.append('"');
	}

	public interface ParameterProvider {
		String getParameter(String name);
	}

	private interface RequestSupplier {
		UpdateRuntimeLogLevels get();
	}

}
