package com.braintribe.gm.graphfetching.test.model.data;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface DataManagement extends GenericEntity {

	EntityType<DataManagement> T = EntityTypes.T(DataManagement.class);

	String sources = "sources";
	String resources = "resources";
	
	Set<DataSource> getSources();
	void setSources(Set<DataSource> sources);
	
	Set<DataResource> getResources();
	void setResources(Set<DataResource> resources);
}
