package com.braintribe.gm.graphfetching.test.model;

import java.util.Date;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Document extends GenericEntity {

	EntityType<Document> T = EntityTypes.T(Document.class);
	
	String getName();
	void setName(String name);
	
	Date getCreatedAt();
	void setCreatedAt(Date greatedAt);

	Set<String> getTags();
	void setTags(Set<String> tags);

	String getText();
	void setText(String text);
}
