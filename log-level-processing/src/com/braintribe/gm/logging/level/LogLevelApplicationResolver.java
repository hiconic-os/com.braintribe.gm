package com.braintribe.gm.logging.level;

import java.util.Set;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.service.api.InstanceId;

public interface LogLevelApplicationResolver {
	Set<String> liveApplications();
	Maybe<InstanceId> resolveApplication(String applicationId);
}
