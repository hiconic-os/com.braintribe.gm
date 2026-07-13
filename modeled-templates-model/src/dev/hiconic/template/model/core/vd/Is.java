package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

import dev.hiconic.template.model.core.TypeReference;

/** Tests whether a runtime value is an instance of a reflected target type. */
@PositionalArguments({"operand", "target"})
public interface Is extends BooleanDescriptor {
	EntityType<Is> T = EntityTypes.T(Is.class);
	PropertyLiteral operand = PropertyLiteral.of(T, "operand");
	PropertyLiteral target = PropertyLiteral.of(T, "target");

	Object getOperand();
	void setOperand(Object operand);
	TypeReference getTarget();
	void setTarget(TypeReference target);
}
