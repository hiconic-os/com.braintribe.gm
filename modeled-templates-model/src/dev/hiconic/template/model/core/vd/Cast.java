package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TypeReference;

@PositionalArguments({"operand", "target"})
public interface Cast extends UnaryOperation {
	EntityType<Cast> T = EntityTypes.T(Cast.class);
	PropertyLiteral target = PropertyLiteral.of(T, "target");

	TypeReference getTarget();
	void setTarget(TypeReference target);
}
