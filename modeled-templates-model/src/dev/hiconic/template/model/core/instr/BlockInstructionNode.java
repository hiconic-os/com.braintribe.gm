package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;

public interface BlockInstructionNode extends InstructionNode {
	EntityType<BlockInstructionNode> T = EntityTypes.T(BlockInstructionNode.class);
	
	PropertyLiteral block = PropertyLiteral.of(T, "block");
	
	TemplateNode getBlock();
	void setBlock(TemplateNode block);
}
