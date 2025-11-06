package com.braintribe.gm.graphfetching.test.model;

import java.util.Date;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Person extends GenericEntity {

	EntityType<Person> T = EntityTypes.T(Person.class);
	
	String firstName = "firstName";
	String lastName = "lastName";
	String gender = "gender";
	String birthday = "birthday";
	String address = "address";
	String bestFriend = "bestFriend";

	String getFirstName();
	void setFirstName(String firstName);
	
	String getLastName();
	void setLastName(String lastName);
	
	Gender getGender();
	void setGender(Gender gender);
	
	Date getBirthday();
	void setBirthday(Date birthday);
	
	Address getAddress();
	void setAddress(Address address);
	
	Person getBestFriend();
	void setBestFriend(Person bestFriend);
}
