package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@Abstract
public interface ArithmeticOperation extends ExplicitlyTypedDescriptor {
	EntityType<ArithmeticOperation> T = EntityTypes.T(ArithmeticOperation.class);

	PropertyLiteral left = PropertyLiteral.of(T, "left");
	PropertyLiteral right = PropertyLiteral.of(T, "right");

	Object getLeft();
	void setLeft(Object left);

	Object getRight();
	void setRight(Object right);
}
