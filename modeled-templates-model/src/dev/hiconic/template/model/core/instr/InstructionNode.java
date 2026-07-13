package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface InstructionNode extends DirectiveNode {
	EntityType<InstructionNode> T = EntityTypes.T(InstructionNode.class);
}
