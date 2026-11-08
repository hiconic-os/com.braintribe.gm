package com.braintribe.gm.model.logging.level.api;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum LogLevelProfileOrderMutation implements EnumBase<LogLevelProfileOrderMutation> {
	APPEND,
	INSERT,
	MOVE,
	REMOVE,
	RESET;

	public static final EnumType<LogLevelProfileOrderMutation> T = EnumTypes.T(LogLevelProfileOrderMutation.class);

	@Override
	public EnumType<LogLevelProfileOrderMutation> type() {
		return T;
	}
}
