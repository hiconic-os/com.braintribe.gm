package com.braintribe.gm.graphfetching.test.model;

import java.util.Date;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Document extends GenericEntity {

	EntityType<Document> T = EntityTypes.T(Document.class);
	
	String name = "name";
	String createdAt = "createdAt";
	String tags = "tags";
	String text = "text";
	
	String getName();
	void setName(String name);
	
	Date getCreatedAt();
	void setCreatedAt(Date createdAt);

	Set<String> getTags();
	void setTags(Set<String> tags);

	String getText();
	void setText(String text);
}
