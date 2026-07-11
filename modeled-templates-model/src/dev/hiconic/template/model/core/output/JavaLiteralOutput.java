package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface JavaLiteralOutput extends SafeOutput {
	EntityType<JavaLiteralOutput> T = EntityTypes.T(JavaLiteralOutput.class);
}
