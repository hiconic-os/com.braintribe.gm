package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface XmlEscape extends Transformer {
	EntityType<XmlEscape> T = EntityTypes.T(XmlEscape.class);
}
