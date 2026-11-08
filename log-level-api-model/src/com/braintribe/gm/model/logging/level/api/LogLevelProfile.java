package com.braintribe.gm.model.logging.level.api;

import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** A named set of logger-level assignments exposed by the log-level API. */
public interface LogLevelProfile extends GenericEntity {
	EntityType<LogLevelProfile> T = EntityTypes.T(LogLevelProfile.class);

	String getName();
	void setName(String name);

	Map<String, String> getLevels();
	void setLevels(Map<String, String> levels);
}
