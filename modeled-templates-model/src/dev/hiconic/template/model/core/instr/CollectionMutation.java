package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

/** Shared typed L-value for collection mutation statements. */
@Abstract
public interface CollectionMutation extends StatementInstructionNode {
	EntityType<CollectionMutation> T = EntityTypes.T(CollectionMutation.class);
	PropertyLiteral target = PropertyLiteral.of(T, "target");
	AssignmentTarget getTarget();
	void setTarget(AssignmentTarget target);
}
