package com.braintribe.gm.graphfetching.test.model;

import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface SignableDocument extends Document {

	EntityType<SignableDocument> T = EntityTypes.T(SignableDocument.class);

	String signatures = "signatures";
	
	Set<Signature> getSignatures();
	void setSignatures(Set<Signature> signatures);
}
