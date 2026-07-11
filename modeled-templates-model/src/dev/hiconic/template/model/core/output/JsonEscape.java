package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface JsonEscape extends Transformer {
	EntityType<JsonEscape> T = EntityTypes.T(JsonEscape.class);
}
