package dev.hiconic.template.model.core;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.output.SafeOutput;

public interface OutputNode extends TemplateNode {
	EntityType<OutputNode> T = EntityTypes.T(OutputNode.class);
	
	PropertyLiteral output = PropertyLiteral.of(T, "output");
	
	SafeOutput getOutput();
	void setOutput(SafeOutput output);
}
