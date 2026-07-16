package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;

/** Information carrier for a named sub-block owned by a surrounding block instruction. */
public interface BlockClause extends GenericEntity {
	EntityType<BlockClause> T = EntityTypes.T(BlockClause.class);

	PropertyLiteral block = PropertyLiteral.of(T, "block");

	TemplateNode getBlock();
	void setBlock(TemplateNode block);
}
