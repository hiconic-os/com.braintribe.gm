package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;
/** A directive owning a primary block and the bindings visible in that block. */
@Abstract
public interface BlockNode extends VariableDefiningNode {
	EntityType<BlockNode> T = EntityTypes.T(BlockNode.class);
	PropertyLiteral block = PropertyLiteral.of(T, "block");

	TemplateNode getBlock();
	void setBlock(TemplateNode block);
}
