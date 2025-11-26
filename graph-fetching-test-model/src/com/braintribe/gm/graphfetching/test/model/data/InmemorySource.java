package com.braintribe.gm.graphfetching.test.model.data;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface InmemorySource extends DataSource {

	EntityType<InmemorySource> T = EntityTypes.T(InmemorySource.class);

	String binaryData = "binaryData";
	String large = "large";
	
	StringEncodedBinaryData getBinaryData();
	void setBinaryData(StringEncodedBinaryData binaryData);
	
	boolean getLarge();
	void setLarge(boolean large);
}
