package com.braintribe.gm.graphfetching.test.model.data;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface SourceInfo extends GenericEntity {

	EntityType<SourceInfo> T = EntityTypes.T(SourceInfo.class);

	String size = "size";
	String hash = "hash";
	
	int getSize();
	void setSize(int size);
	
	String getHash();
	void setHash(String hash);
}
