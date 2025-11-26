package com.braintribe.gm.graphfetching.test.model.data;

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface DataResource extends HasName {

	EntityType<DataResource> T = EntityTypes.T(DataResource.class);

	String creationInfo = "creationInfo";
	String source = "source";
	String tags = "tags";
	String altNames = "altNames";
	
	CreationInfo getCreationInfo();
	void setCreationInfo(CreationInfo creationInfo);
	
	DataSource getSource();
	void setSource(DataSource source);
	
	Set<String> getTags();
	void setTags(Set<String> tags);
	
	List<String> getAltNames();
	void setAltNames(List<String> altNames);
}
