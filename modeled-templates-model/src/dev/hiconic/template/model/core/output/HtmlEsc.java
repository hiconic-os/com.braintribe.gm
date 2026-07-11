package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface HtmlEsc extends Transformer {
	EntityType<HtmlEsc> T = EntityTypes.T(HtmlEsc.class);
}
