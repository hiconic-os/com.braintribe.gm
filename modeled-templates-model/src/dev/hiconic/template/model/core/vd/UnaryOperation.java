package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import dev.hiconic.template.model.core.TypeReference;

/** An ordinary unary value descriptor; the pipe operator does not depend on this type. */
@Abstract
public interface UnaryOperation extends ExplicitlyTypedDescriptor {
	EntityType<UnaryOperation> T = EntityTypes.T(UnaryOperation.class);
	PropertyLiteral operand = PropertyLiteral.of(T, "operand");
	PropertyLiteral inputType = PropertyLiteral.of(T, "inputType");

	Object getOperand();
	void setOperand(Object operand);
	TypeReference getInputType();
	void setInputType(TypeReference inputType);
}
