package dev.hiconic.template.model.core;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface SequenceNode extends TemplateNode {
	EntityType<SequenceNode> T = EntityTypes.T(SequenceNode.class);
	
	PropertyLiteral nodes = PropertyLiteral.of(T, "nodes");
	
	List<TemplateNode> getNodes();
	void setNodes(List<TemplateNode> nodes);
}
