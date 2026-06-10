package com.braintribe.gm.logging.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.gm.model.logging.level.api.UpdateEffectiveLogLevels;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.logging.Logger;
import com.braintribe.logging.level.LogLevelManager;
import com.braintribe.logging.level.LogLevelUpdateDispatcher;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.model.service.api.MulticastRequest;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Failure;
import com.braintribe.model.service.api.result.MulticastResponse;
import com.braintribe.model.service.api.result.ServiceResult;
import com.braintribe.model.service.api.result.Unsatisfied;

public class MulticastingLogLevelUpdateDispatcher implements LogLevelUpdateDispatcher {
	private static final Logger logger = Logger.getLogger(MulticastingLogLevelUpdateDispatcher.class);

	private Evaluator<ServiceRequest> evaluator;
	private InstanceId localInstanceId;
	private LogLevelManager logLevelManager;
	private Long timeoutInMs;

	@Override
	public Maybe<Void> dispatchUpdate() {
		try {
			Objects.requireNonNull(logLevelManager, "logLevelManager");

			UpdateEffectiveLogLevels update = UpdateEffectiveLogLevels.T.create();
			MulticastRequest multicast = MulticastRequest.T.create();

			multicast.setAsynchronous(false);
			multicast.setServiceRequest(update);
			multicast.setAddressee(InstanceId.of(null, localInstanceId.getApplicationId()));

			if (timeoutInMs != null) {
				multicast.setTimeout(timeoutInMs);
			}

			MulticastResponse response = multicast.eval(evaluator).get();
			return validate(response);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to multicast effective log level update").asMaybe();
		}
	}

	private Maybe<Void> validate(MulticastResponse response) {
		StringBuilder errors = new StringBuilder();
		List<Reason> reasons = new ArrayList<>();

		for (Map.Entry<InstanceId, ServiceResult> entry: response.getResponses().entrySet()) {
			ServiceResult result = entry.getValue();

			if (result instanceof Failure) {
				Failure failure = (Failure) result;
				String message = entry.getKey() + ": " + failure.getType() + ": " + failure.getDetails();
				appendError(errors, message);
				reasons.add(InternalError.create(message));
			} else if (result instanceof Unsatisfied) {
				Unsatisfied unsatisfied = (Unsatisfied) result;
				Reason reason = unsatisfied.getWhy();
				String message = entry.getKey() + ": " + (reason == null ? "unsatisfied" : reason.asString());
				appendError(errors, message);

				if (reason != null) {
					reasons.add(reason);
				} else {
					reasons.add(InternalError.create(message));
				}
			}
		}

		if (errors.length() > 0) {
			String message = "Failed to update effective log levels on at least one cluster instance:" + errors;
			InternalError reason = InternalError.create(message);
			reason.getReasons().addAll(reasons);
			InternalError internalError = InternalError.createTraceback(reason, "Failed to update effective log levels", logger::error);

			return Maybe.empty(internalError);
		}

		return Maybe.complete(null);
	}

	private void appendError(StringBuilder errors, String message) {
		errors.append("\n- ").append(message);
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
	public void setTimeoutInMs(Long timeoutInMs) {
		this.timeoutInMs = timeoutInMs;
	}

	@Override
	@Configurable
	@Required
	public void setLogLevelManager(LogLevelManager logLevelManager) {
		this.logLevelManager = Objects.requireNonNull(logLevelManager, "logLevelManager");
	}
}
