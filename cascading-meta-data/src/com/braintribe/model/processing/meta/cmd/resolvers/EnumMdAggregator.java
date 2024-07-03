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
package com.braintribe.model.processing.meta.cmd.resolvers;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.processing.index.ConcurrentCachedIndex;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.StaticContext;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmEnumTypeAspect;
import com.braintribe.model.processing.meta.cmd.extended.EnumMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EnumRelatedMdDescriptor;
import com.braintribe.model.processing.meta.cmd.index.MetaDataIndexKey;
import com.braintribe.model.processing.meta.cmd.index.MetaDataIndexStructure.EnumMdIndex;
import com.braintribe.model.processing.meta.cmd.index.MetaDataIndexStructure.MdIndex;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataBox;
import com.braintribe.model.processing.meta.oracle.EnumTypeOracle;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;

/**
 * A resolver of {@link MetaData} for concrete {@link GmEnumType}
 */
public class EnumMdAggregator extends MdAggregator {

	private final StaticContext localContext;
	private final EnumMdIndex enumMdIndex;

	private final ConstantMdAggregatorIndex constantMdAggregatorIndex = new ConstantMdAggregatorIndex();

	private class ConstantMdAggregatorIndex extends ConcurrentCachedIndex<String, ConstantMdAggregator> {
		@Override
		protected ConstantMdAggregator provideValueFor(String constant) {
			return new ConstantMdAggregator(EnumMdAggregator.this, enumMdIndex.acquireEnumConstantMdIndex(constant));
		}
	}

	public EnumMdAggregator(ModelMdAggregator parent, EnumMdIndex enumMdIndex) {
		super(parent.resolutionContext, EnumMdDescriptor.T);

		this.enumMdIndex = enumMdIndex;
		this.localContext = new StaticContext(parent.localContext(), localApects());
	}

	public EnumTypeOracle getEnumOracle() {
		return enumMdIndex.enumOracle;
	}

	private Map<Class<? extends SelectorContextAspect<?>>, Object> localApects() {
		GmEnumType gmEnumType = getGmEnumType();

		Map<Class<? extends SelectorContextAspect<?>>, Object> result = new HashMap<Class<? extends SelectorContextAspect<?>>, Object>();
		result.put(GmEnumTypeAspect.class, gmEnumType);

		return result;
	}

	public GmEnumType getGmEnumType() {
		return enumMdIndex.getGmEnumType();
	}

	public ConstantMdAggregator acquireConstantMdAggregator(String constant) {
		return constantMdAggregatorIndex.acquireFor(constant);
	}

	@Override
	protected List<MetaDataBox> acquireFullMetaData(EntityType<? extends MetaData> metaDataType, boolean extended) {
		/* Regarding the key, see comment inside ModelMetaDataResolver#acquireFullMetaData(Class) */
		MetaDataIndexKey mdKey = MetaDataIndexKey.forAll(metaDataType);

		List<MetaDataBox> result = newList();
		result.add(acquireMetaData(enumMdIndex, mdKey, extended));
		result.add(acquireMetaData(enumMdIndex.modelMdIndex.modelEnumMdIndex, mdKey, extended));

		return result;
	}

	@Override
	protected EnumRelatedMdDescriptor extendedFor(QualifiedMetaData qmd, MdIndex ownerIndex) {
		MetaData md = qmd.metaData();
		GmModelElement ownerElement = qmd.ownerElement();

		if (ownerElement == null) {
			return MetaDataWrapper.forEnumType(md, null, null);

		} else if (ownerElement instanceof GmEnumTypeInfo) {

			GmEnumTypeInfo ownerTypeInfo = (GmEnumTypeInfo) ownerElement;
			return MetaDataWrapper.forEnumType(md, ownerTypeInfo.declaringModel(), ownerTypeInfo);

		} else if (ownerElement instanceof GmMetaModel) {
			GmMetaModel ownerModel = (GmMetaModel) ownerElement;
			return MetaDataWrapper.forEnumType(md, ownerModel, null);

		} else {
			throw new IllegalArgumentException("Unexpected owner for enum MD: " + ownerElement);
		}
	}

	@Override
	protected StaticContext localContext() {
		return localContext;
	}

}
