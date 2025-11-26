package com.braintribe.gm.graphfetching.api.node;

public enum KeyValueType {
	SCALAR_SCALAR(false, false), 
	SCALAR_ENTITY(false, true), 
	ENTITY_SCALAR(true, false), 
	ENTITY_ENTITY(true, true);
	
	private final boolean entityKey;
	private final boolean entityValue;

	private KeyValueType(boolean entityKey, boolean entityValue) {
		this.entityKey = entityKey;
		this.entityValue = entityValue;
	}
	
	public boolean isEntityRelated() {
		return entityKey || entityValue;
	}
	
	public boolean isEntityKey() {
		return entityKey;
	}
	
	public boolean isEntityValue() {
		return entityValue;
	}
}
