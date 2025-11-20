package com.braintribe.gm.graphfetching.test.model.data;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface FileReference extends HasName {

	EntityType<FileReference> T = EntityTypes.T(FileReference.class);

	String path = "path";
	
	String getPath();
	void setPath(String path);
}
