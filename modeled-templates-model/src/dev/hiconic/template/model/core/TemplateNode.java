package dev.hiconic.template.model.core;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface TemplateNode extends GenericEntity {
	EntityType<TemplateNode> T = EntityTypes.T(TemplateNode.class);
}
