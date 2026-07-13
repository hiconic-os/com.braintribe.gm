package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
public interface BlockInstructionNode extends InstructionNode, BlockNode {
	EntityType<BlockInstructionNode> T = EntityTypes.T(BlockInstructionNode.class);
}
