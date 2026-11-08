package com.braintribe.gm.model.logging.level.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface LogLevelState extends GenericEntity {
	EntityType<LogLevelState> T = EntityTypes.T(LogLevelState.class);

	Map<String, String> getEffectiveLevels();
	void setEffectiveLevels(Map<String, String> effectiveLevels);

	Map<String, String> getRuntimeLevels();
	void setRuntimeLevels(Map<String, String> runtimeLevels);

	Map<String, String> getPackagedLevels();
	void setPackagedLevels(Map<String, String> packagedLevels);

	List<LogLevelProfile> getPackagedProfiles();
	void setPackagedProfiles(List<LogLevelProfile> packagedProfiles);

	List<LogLevelProfile> getRuntimeProfiles();
	void setRuntimeProfiles(List<LogLevelProfile> runtimeProfiles);

	List<String> getEffectiveProfileOrder();
	void setEffectiveProfileOrder(List<String> effectiveProfileOrder);

	boolean getRuntimeProfileOrderActive();
	void setRuntimeProfileOrderActive(boolean runtimeProfileOrderActive);

	Set<String> getKnownLoggerNames();
	void setKnownLoggerNames(Set<String> knownLoggerNames);
}
