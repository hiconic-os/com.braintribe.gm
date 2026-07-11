package dev.hiconic.template.test.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface TestAddress extends GenericEntity {
	EntityType<TestAddress> T = EntityTypes.T(TestAddress.class);

	PropertyLiteral city = PropertyLiteral.of(T, "city");
	PropertyLiteral postalCode = PropertyLiteral.of(T, "postalCode");

	String getCity();
	void setCity(String city);

	String getPostalCode();
	void setPostalCode(String postalCode);
}
