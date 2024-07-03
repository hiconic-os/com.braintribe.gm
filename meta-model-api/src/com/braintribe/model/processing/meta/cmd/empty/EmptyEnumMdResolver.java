// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.meta.cmd.empty;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.builders.ConstantMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.EnumMdResolver;
import com.braintribe.model.processing.meta.cmd.result.EnumMdResult;

/**
 * 
 */
public class EmptyEnumMdResolver extends EmptyMdResolver<EnumMdResolver> implements EnumMdResolver {

	public static final EmptyEnumMdResolver INSTANCE = new EmptyEnumMdResolver();

	private EmptyEnumMdResolver() {
	}

	@Override
	public ConstantMdResolver constant(Enum<?> constant) {
		return EmptyConstantMdResolver.INSTANCE;
	}

	@Override
	public ConstantMdResolver constant(GmEnumConstant constant) {
		return EmptyConstantMdResolver.INSTANCE;
	}

	@Override
	public ConstantMdResolver constant(String constant) {
		return EmptyConstantMdResolver.INSTANCE;
	}

	@Override
	public GmEnumType getEnumType() {
		throw new UnsupportedOperationException("Method 'EmptyEnumMmdResolver.getEnumType' is not supported!");
	}

	@Override
	public final <M extends MetaData> EnumMdResult<M> meta(EntityType<M> metaDataType) {
		return EmptyMdResult.EmptyEnumMdResult.singleton();
	}

}
