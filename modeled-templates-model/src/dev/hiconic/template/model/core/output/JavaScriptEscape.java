package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface JavaScriptEscape extends Transformer {
	EntityType<JavaScriptEscape> T = EntityTypes.T(JavaScriptEscape.class);
}
