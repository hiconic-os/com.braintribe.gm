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
package com.braintribe.model.processing.meta.cmd.builders;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.context.MutableSelectorContext;
import com.braintribe.model.processing.meta.cmd.empty.EmptyConstantMdResolver;
import com.braintribe.model.processing.meta.cmd.resolvers.ConstantMdAggregator;
import com.braintribe.model.processing.meta.cmd.resolvers.EnumMdAggregator;
import com.braintribe.model.processing.meta.cmd.result.EnumMdResult;

/**
 * 
 */
public class EnumMdResolverImpl extends MdResolverImpl<EnumMdResolver> implements EnumMdResolver {

	protected EnumMdAggregator enumMdAggregator;

	protected EnumMdResolverImpl(EnumMdAggregator enumMdAggregator, MutableSelectorContext selectorContext) {
		super(EnumMdResolverImpl.class, selectorContext, enumMdAggregator);

		this.enumMdAggregator = enumMdAggregator;
	}

	@Override
	public ConstantMdResolver constant(Enum<?> constant) {
		return constant(constant.name());
	}

	@Override
	public ConstantMdResolver constant(GmEnumConstant constant) {
		return constant(constant.getName());
	}

	@Override
	public ConstantMdResolver constant(String constant) {
		if (enumMdAggregator.getEnumOracle().findConstant(constant) == null) {
			return lenientOrThrowException(() -> EmptyConstantMdResolver.INSTANCE, () -> "Constant not found: " + constant);
		}

		ConstantMdAggregator constantMdAggregator = enumMdAggregator.acquireConstantMdAggregator(constant);
		constantMdAggregator.addLocalContextTo(selectorContext);

		return getEnumConstantMetaDataContextBuilder(constantMdAggregator);
	}

	@Override
	public GmEnumType getEnumType() {
		return enumMdAggregator.getGmEnumType();
	}

	protected ConstantMdResolver getEnumConstantMetaDataContextBuilder(ConstantMdAggregator constantMdAggregator) {
		return new ConstantMdResolverImpl(constantMdAggregator, selectorContext);
	}

	@Override
	public EnumMdResolver fork() {
		return new EnumMdResolverImpl(enumMdAggregator, selectorContext.copy());
	}

	@Override
	public <M extends MetaData> EnumMdResult<M> meta(EntityType<M> metaDataType) {
		return new MdResultImpl.EnumMdResultImpl<>(metaDataType, enumMdAggregator, selectorContext);
	}

}
