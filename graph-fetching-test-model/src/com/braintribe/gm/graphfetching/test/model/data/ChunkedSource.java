package com.braintribe.gm.graphfetching.test.model.data;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface ChunkedSource extends DataSource {

	EntityType<ChunkedSource> T = EntityTypes.T(ChunkedSource.class);

	String chunks = "chunks";
	
	List<DataSource> getChunks();
	void setChunks(List<DataSource> chunks);
	
}
