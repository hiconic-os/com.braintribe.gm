package dev.hiconic.template.model.core.decl;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import dev.hiconic.template.model.core.instr.DirectiveNode;

@Abstract
public interface DeclarationNode extends DirectiveNode {
	EntityType<DeclarationNode> T = EntityTypes.T(DeclarationNode.class);
}
