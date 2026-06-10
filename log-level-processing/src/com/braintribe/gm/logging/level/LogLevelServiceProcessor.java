package com.braintribe.gm.logging.level;

import static com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling.getOrTunnel;

import java.util.Objects;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.gm.model.logging.level.api.GetLogLevelState;
import com.braintribe.gm.model.logging.level.api.LogLevelRequest;
import com.braintribe.gm.model.logging.level.api.LogLevelState;
import com.braintribe.gm.model.logging.level.api.UpdateRuntimeLogLevels;
import com.braintribe.gm.model.logging.level.api.UpdateEffectiveLogLevels;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.logging.level.LogLevelManager;
import com.braintribe.logging.level.LogLevelRuntimeUpdater;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.impl.AbstractDispatchingServiceProcessor;
import com.braintribe.model.processing.service.impl.DispatchConfiguration;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.UnicastRequest;
import com.braintribe.model.service.api.result.Neutral;

public class LogLevelServiceProcessor extends AbstractDispatchingServiceProcessor<LogLevelRequest, Object> {
	private LogLevelManager logLevelManager;
	private LogLevelRuntimeUpdater logLevelRuntimeUpdater;
	private Evaluator<ServiceRequest> evaluator;
	private InstanceId localInstanceId;
	private LogLevelApplicationResolver applicationResolver;
	
	@Required
	public void setLogLevelManager(LogLevelManager logLevelManager) {
		this.logLevelManager = Objects.requireNonNull(logLevelManager, "logLevelManager");
	}

	@Required
	public void setLogLevelRuntimeUpdater(LogLevelRuntimeUpdater logLevelRuntimeUpdater) {
		this.logLevelRuntimeUpdater = Objects.requireNonNull(logLevelRuntimeUpdater, "logLevelRuntimeUpdater");
	}

	@Configurable
	@Required
	public void setEvaluator(Evaluator<ServiceRequest> evaluator) {
		this.evaluator = Objects.requireNonNull(evaluator, "evaluator");
	}

	@Configurable
	@Required
	public void setLocalInstanceId(InstanceId localInstanceId) {
		this.localInstanceId = Objects.requireNonNull(localInstanceId, "localInstanceId");
	}

	@Configurable
	@Required
	public void setApplicationResolver(LogLevelApplicationResolver applicationResolver) {
		this.applicationResolver = Objects.requireNonNull(applicationResolver, "applicationResolver");
	}

	@Override
	protected void configureDispatching(DispatchConfiguration<LogLevelRequest, Object> dispatching) {
		dispatching.registerReasoned(GetLogLevelState.T, this::getLogLevelState);
		dispatching.registerReasoned(UpdateRuntimeLogLevels.T, this::updateRuntimeLogLevels);
		dispatching.registerReasoned(UpdateEffectiveLogLevels.T, this::updateEffectiveLogLevels);
	}

	@Override
	public Object process(ServiceRequestContext requestContext, LogLevelRequest request) {
		String applicationId = normalize(request.getApplicationId());
		if (isLocalTarget(applicationId)) {
			return super.process(requestContext, request);
		}

		UnicastRequest unicast = UnicastRequest.T.create();
		unicast.setAddressee(getOrTunnel(applicationResolver.resolveApplication(applicationId)));
		unicast.setServiceRequest(request);
		return getOrTunnel(unicast.eval(evaluator).getReasoned());
	}

	private Maybe<LogLevelState> getLogLevelState(ServiceRequestContext context, GetLogLevelState request) {
		LogLevelState state = LogLevelState.T.create();
		state.setEffectiveLevels(logLevelManager.getEffectiveLogLevels());
		state.setRuntimeLevels(logLevelManager.getRuntimeLogLevels());
		state.setPackagedLevels(logLevelManager.getPackagedLogLevels());
		state.setKnownLoggerNames(logLevelManager.getKnownLoggerNames());
		return Maybe.complete(state);
	}

	private Maybe<Neutral> updateRuntimeLogLevels(ServiceRequestContext context, UpdateRuntimeLogLevels request) {
		Maybe<Void> update = request.getClearAll() ? logLevelRuntimeUpdater.clearRuntimeLogLevels()
				: logLevelRuntimeUpdater.updateRuntimeLogLevels(request.getLevels(), request.getNamesToRemove());

		if (update.isUnsatisfied()) {
			return update.propagateReason();
		}

		return Maybe.complete(Neutral.NEUTRAL);
	}

	private Maybe<Neutral> updateEffectiveLogLevels(ServiceRequestContext context, UpdateEffectiveLogLevels request) {
		try {
			logLevelManager.applyEffectiveLogLevels();
			return Maybe.complete(Neutral.NEUTRAL);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to update effective log levels").asMaybe();
		}
	}

	private boolean isLocalTarget(String applicationId) {
		return applicationId == null || localInstanceId != null && applicationId.equals(localInstanceId.getApplicationId());
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}

		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
