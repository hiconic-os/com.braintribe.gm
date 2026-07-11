package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface JavaLiteralEscape extends Transformer {
	EntityType<JavaLiteralEscape> T = EntityTypes.T(JavaLiteralEscape.class);
}
