package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface VariableAssignmentTarget extends AssignmentTarget {
	EntityType<VariableAssignmentTarget> T = EntityTypes.T(VariableAssignmentTarget.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");

	dev.hiconic.template.model.core.Symbol getSymbol();
	void setSymbol(dev.hiconic.template.model.core.Symbol symbol);
}
