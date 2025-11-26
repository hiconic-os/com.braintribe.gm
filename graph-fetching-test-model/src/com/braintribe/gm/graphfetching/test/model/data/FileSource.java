package com.braintribe.gm.graphfetching.test.model.data;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface FileSource extends DataSource {

	EntityType<FileSource> T = EntityTypes.T(FileSource.class);

	String reference = "reference";
	String extension = "extension";
	
	FileReference getReference();
	void setReference(FileReference reference);
	
	String getExtension();
	void setExtension(String extension);
}
