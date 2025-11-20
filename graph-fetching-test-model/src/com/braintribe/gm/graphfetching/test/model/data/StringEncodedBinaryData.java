package com.braintribe.gm.graphfetching.test.model.data;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface StringEncodedBinaryData extends GenericEntity {

	EntityType<StringEncodedBinaryData> T = EntityTypes.T(StringEncodedBinaryData.class);

	String data = "data";
	String encoding = "encoding";
	
	String getEncoding();
	void setEncoding(String encoding);
	
	String getData();
	void setData(String data);
}
