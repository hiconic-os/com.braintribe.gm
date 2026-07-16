package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.IntegerDescriptor;

import dev.hiconic.template.model.core.TypeReference;

/** Returns the size of a collection or map as integer. */
@PositionalArguments("operand")
public interface Size extends IntegerDescriptor {
	EntityType<Size> T = EntityTypes.T(Size.class);

	PropertyLiteral operand = PropertyLiteral.of(T, "operand");
	PropertyLiteral inputType = PropertyLiteral.of(T, "inputType");

	Object getOperand();
	void setOperand(Object operand);

	TypeReference getInputType();
	void setInputType(TypeReference inputType);
}
