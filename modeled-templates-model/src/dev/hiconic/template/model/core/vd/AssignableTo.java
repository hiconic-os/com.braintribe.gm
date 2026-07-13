package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

import dev.hiconic.template.model.core.TypeReference;

/** Tests reflected type assignability: target.isAssignableFrom(source). */
@PositionalArguments({"source", "target"})
public interface AssignableTo extends BooleanDescriptor {
	EntityType<AssignableTo> T = EntityTypes.T(AssignableTo.class);
	PropertyLiteral source = PropertyLiteral.of(T, "source");
	PropertyLiteral target = PropertyLiteral.of(T, "target");

	TypeReference getSource();
	void setSource(TypeReference source);
	TypeReference getTarget();
	void setTarget(TypeReference target);
}
