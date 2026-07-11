package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface JavaScriptOutput extends SafeOutput {
	EntityType<JavaScriptOutput> T = EntityTypes.T(JavaScriptOutput.class);
}
