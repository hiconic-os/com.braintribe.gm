package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface JsonOutput extends SafeOutput {
	EntityType<JsonOutput> T = EntityTypes.T(JsonOutput.class);
}
