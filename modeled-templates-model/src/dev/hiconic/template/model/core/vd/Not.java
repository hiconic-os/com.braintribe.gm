package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

public interface Not extends BooleanDescriptor {
	EntityType<Not> T = EntityTypes.T(Not.class);
	
	PropertyLiteral operand = PropertyLiteral.of(T, "operand");
	
	boolean getOperand();
	void setOperand(boolean operand);
}
