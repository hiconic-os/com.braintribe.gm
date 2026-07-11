package dev.hiconic.template.test.model;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.vd.ExplicitlyTypedDescriptor;

public interface TestCompletedDescriptor extends ExplicitlyTypedDescriptor {
	EntityType<TestCompletedDescriptor> T = EntityTypes.T(TestCompletedDescriptor.class);

	PropertyLiteral value = PropertyLiteral.of(T, "value");

	Object getValue();
	void setValue(Object value);
}
