package dev.hiconic.template.test.model;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum TestColor implements EnumBase<TestColor> {
	red,
	green,
	blue;

	public static final EnumType<TestColor> T = EnumTypes.T(TestColor.class);

	@Override
	public EnumType<TestColor> type() {
		return T;
	}
}
