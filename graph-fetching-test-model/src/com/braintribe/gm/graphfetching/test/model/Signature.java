package com.braintribe.gm.graphfetching.test.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Signature extends GenericEntity {

	EntityType<Signature> T = EntityTypes.T(Signature.class);

	String hash = "hash"; 
	String signature = "signature"; 
	
	String getHash();
	void setHash(String hash);
	
	String getSignature();
	void setSignature(String string);
}
