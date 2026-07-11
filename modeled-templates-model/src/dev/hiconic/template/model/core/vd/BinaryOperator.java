package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

public interface BinaryOperator extends BooleanDescriptor {
	EntityType<BinaryOperator> T = EntityTypes.T(BinaryOperator.class);
	
	PropertyLiteral left = PropertyLiteral.of(T, "left");
	PropertyLiteral right = PropertyLiteral.of(T, "right");
	
	Object getLeft();
	void setLeft(Object left);
	
	Object getRight();
	void setRight(Object right);
}
