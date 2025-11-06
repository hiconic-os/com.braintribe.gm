package com.braintribe.gm.graphfetching.test.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Address extends GenericEntity {

	EntityType<Address> T = EntityTypes.T(Address.class);

	String city = "city";
	String street = "street";
	String streetNumber = "streetNumber";
	
	City getCity();
	void setCity(City city);
	
	String getStreet();
	void setStreet(String street);
	
	String getStreetNumber();
	void setStreetNumber(String streetNumber);
}
