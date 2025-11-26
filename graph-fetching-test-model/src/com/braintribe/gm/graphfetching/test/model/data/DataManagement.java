package com.braintribe.gm.graphfetching.test.model.data;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface DataManagement extends GenericEntity {

	EntityType<DataManagement> T = EntityTypes.T(DataManagement.class);

	String sources = "sources";
	String resources = "resources";
	String resourcesByName = "resourcesByName";
	String sourceHashes = "sourceHashes";
	String sourceOccurrences = "sourceOccurrences";
	String lableRatings = "lableRatings";
	
	Set<DataSource> getSources();
	void setSources(Set<DataSource> sources);
	
	Set<DataResource> getResources();
	void setResources(Set<DataResource> resources);

	Map<String, Double> getLableRatings();
	void setLableRatings(Map<String, Double> lableRatings);
	
	Map<String, DataResource> getResourcesByName();
	void setResourcesByName(Map<String, DataResource> resourcesByName);
	
	Map<DataSource, String> getSourceHashes();
	void setSourceHashes(Map<DataSource, String> sourceHashes);
	
	Map<DataSource, DataResource> getSourceOccurrences();
	void setSourceOccurrences(Map<DataSource, DataResource> sourceOccurrences);
}
