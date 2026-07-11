package dev.hiconic.template.model.core.vd;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

public interface BooleanJunction extends BooleanDescriptor {
	EntityType<BooleanJunction> T = EntityTypes.T(BooleanJunction.class);
	
	PropertyLiteral operands = PropertyLiteral.of(T, "operands");
	
	List<Boolean> getOperands();
	void setOperands(List<Boolean> operands);
}
