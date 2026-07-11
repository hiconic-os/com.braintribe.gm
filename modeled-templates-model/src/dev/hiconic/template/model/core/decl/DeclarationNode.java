package dev.hiconic.template.model.core.decl;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import dev.hiconic.template.model.core.TemplateNode;

@Abstract
public interface DeclarationNode extends TemplateNode {
	EntityType<DeclarationNode> T = EntityTypes.T(DeclarationNode.class);
}
