package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface CssEscape extends Transformer {
	EntityType<CssEscape> T = EntityTypes.T(CssEscape.class);
}
