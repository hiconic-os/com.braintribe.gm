package com.braintribe.gm.graphfetching.test.model;

import java.util.Date;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Company extends GenericEntity {

	EntityType<Company> T = EntityTypes.T(Company.class);
	
	String getName();
	void setName(String name);
	
	Date getFoundedAt();
	void setFoundedAt(Date foundedAt);
	
	Address getAddress();
	void setAddress(Address address);

	Set<Person> getOwners();
	void setOwners(Set<Person> owners);
	
	Set<Person> getCeos();
	void setCeos(Set<Person> owners);
	
	Person getLawyer();
	void setLawyer(Person person);
	
	Set<Document> getContracts();
	void setContracts(Set<Document> contracts);
}
