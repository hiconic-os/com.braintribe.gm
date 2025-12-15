package com.braintribe.gm.graphfetching.test.model.tech;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum Enuma implements EnumBase<Enuma>{
	constant1, constant2, constant3, constant5, constant6;
	public static final EnumType<Enuma> T = EnumTypes.T(Enuma.class);
	
	@Override
	public EnumType<Enuma> type() {
		return T;
	}
}
