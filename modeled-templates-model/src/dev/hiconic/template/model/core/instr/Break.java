package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

/** Exits the nearest enclosing runtime {@link BreakableNode}. */
public interface Break extends StatementInstructionNode {
	EntityType<Break> T = EntityTypes.T(Break.class);

	PropertyLiteral flowControlBound = PropertyLiteral.of(T, "flowControlBound");

	Boolean getFlowControlBound();
	void setFlowControlBound(Boolean flowControlBound);
}
