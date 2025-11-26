package com.braintribe.gm.graphfetching.test.model.data;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum ChunkQuality implements EnumBase<ChunkQuality>{
	uniform, mixed;
	public static final EnumType<ChunkQuality> T = EnumTypes.T(ChunkQuality.class);
	
	@Override
	public EnumType<ChunkQuality> type() {
		return T;
	}
}
