package com.braintribe.codec.marshaller.json.buffer;

import com.braintribe.codec.marshaller.api.IdentityManagementMode;

public enum SpecialField {
	id(true, IdentityManagementMode.id),
	_id(false, IdentityManagementMode._id),
	_ref(false),
	_type(false),
	globalId(true);
	
	public final boolean isProperty;
	public final IdentityManagementMode inferredIdentityManagementMode;
	
	private SpecialField(boolean isProperty) {
		this(isProperty, null);
	}
	
	private SpecialField(boolean isProperty, IdentityManagementMode inferredIdentityManagementMode) {
		this.isProperty = isProperty;
		this.inferredIdentityManagementMode = inferredIdentityManagementMode;
	}
	
	public static SpecialField find(String name) {
		SpecialField candidate = null;
		switch (name.length()) {
			case 2: candidate = id; break;
			case 3: candidate = _id; break;
			case 4: candidate = _ref; break;
			case 5: candidate = _type; break;
			case 8: candidate = globalId; break;
		}
		
		if (candidate == null)
			return null;
		
		return candidate.name().equals(name)? candidate: null;
	}
}
