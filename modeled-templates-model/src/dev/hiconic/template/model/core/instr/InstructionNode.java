package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import dev.hiconic.template.model.core.TemplateNode;

public interface InstructionNode extends TemplateNode {
	EntityType<InstructionNode> T = EntityTypes.T(InstructionNode.class);
}
