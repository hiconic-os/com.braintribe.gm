package dev.hiconic.template.test.model.demo;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum SessionLevel implements EnumBase<SessionLevel> {
	introductory,
	intermediate,
	advanced;

	public static final EnumType<SessionLevel> T = EnumTypes.T(SessionLevel.class);

	@Override
	public EnumType<SessionLevel> type() {
		return T;
	}
}
