package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

/** Continues the nearest enclosing runtime {@link ContinuableNode}. */
public interface Continue extends StatementInstructionNode {
	EntityType<Continue> T = EntityTypes.T(Continue.class);

	PropertyLiteral flowControlBound = PropertyLiteral.of(T, "flowControlBound");

	Boolean getFlowControlBound();
	void setFlowControlBound(Boolean flowControlBound);
}
