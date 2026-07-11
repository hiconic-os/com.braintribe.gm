package dev.hiconic.template.model.core;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@Abstract
public interface ArgumentedNode extends TemplateNode {
	EntityType<ArgumentedNode> T = EntityTypes.T(ArgumentedNode.class);
	
	PropertyLiteral node = PropertyLiteral.of(T, "node");
	
	TemplateNode getNode();
	void setNode(TemplateNode node);
}
