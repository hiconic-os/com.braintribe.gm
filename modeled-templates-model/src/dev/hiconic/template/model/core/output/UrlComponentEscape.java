package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface UrlComponentEscape extends Transformer {
	EntityType<UrlComponentEscape> T = EntityTypes.T(UrlComponentEscape.class);
}
