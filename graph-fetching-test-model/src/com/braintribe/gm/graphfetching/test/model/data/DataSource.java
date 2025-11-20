package com.braintribe.gm.graphfetching.test.model.data;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface DataSource extends GenericEntity {

	EntityType<DataSource> T = EntityTypes.T(DataSource.class);

	String info = "info";
	
	SourceInfo getInfo();
	void setInfo(SourceInfo info);
	
}
