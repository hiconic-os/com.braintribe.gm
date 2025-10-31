package com.braintribe.gm.graphfetching.test.model;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum Gender implements EnumBase<Gender> {
	male, female, diverse;
	
	public static final EnumType<Gender> T = EnumTypes.T(Gender.class);
	
	@Override
	public EnumType<Gender> type() {
		return T;
	}
}
