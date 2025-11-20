package com.braintribe.gm.graphfetching.test.model.data;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface DataResource extends HasName {

	EntityType<DataResource> T = EntityTypes.T(DataResource.class);

	String creationInfo = "creationInfo";
	String source = "source";
	
	CreationInfo getCreationInfo();
	void setCreationInfo(CreationInfo creationInfo);
	
	DataSource getSource();
	void setSource(DataSource source);
	
}
