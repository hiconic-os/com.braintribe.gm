package com.braintribe.gm.graphfetching.test.model.data;

import java.util.Date;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface CreationInfo extends GenericEntity {

	EntityType<CreationInfo> T = EntityTypes.T(CreationInfo.class);

	String createdAt = "createdAt";
	String createdBy = "createdBy";
	
	Date getCreatedAt();
	void setCreatedAt(Date createdAt);
	
	String getCreatedBy();
	void setCreatedBy(String createdBy);
}
