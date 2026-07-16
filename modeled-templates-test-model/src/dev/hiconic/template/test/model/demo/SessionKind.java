package dev.hiconic.template.test.model.demo;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum SessionKind implements EnumBase<SessionKind> {
	keynote,
	talk,
	workshop,
	panel;

	public static final EnumType<SessionKind> T = EnumTypes.T(SessionKind.class);

	@Override
	public EnumType<SessionKind> type() {
		return T;
	}
}
