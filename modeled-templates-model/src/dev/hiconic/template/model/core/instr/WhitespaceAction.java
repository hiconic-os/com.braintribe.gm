package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum WhitespaceAction implements EnumBase<WhitespaceAction> {
	preserve,
	trim,
	trimLine;

	public static final EnumType<WhitespaceAction> T = EnumTypes.T(WhitespaceAction.class);

	@Override
	public EnumType<WhitespaceAction> type() {
		return T;
	}
}
