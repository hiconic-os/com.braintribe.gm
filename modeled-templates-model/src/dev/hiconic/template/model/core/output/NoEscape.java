package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface NoEscape extends Transformer {
	EntityType<NoEscape> T = EntityTypes.T(NoEscape.class);
}
