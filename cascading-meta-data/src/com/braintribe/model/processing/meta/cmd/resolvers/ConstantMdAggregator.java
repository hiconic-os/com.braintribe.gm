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

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.processing.meta.cmd.context.StaticContext;
import com.braintribe.model.processing.meta.cmd.extended.ConstantMdDescriptor;
import com.braintribe.model.processing.meta.cmd.index.MetaDataIndexKey;
import com.braintribe.model.processing.meta.cmd.index.MetaDataIndexStructure.EnumConstantMdIndex;
import com.braintribe.model.processing.meta.cmd.index.MetaDataIndexStructure.MdIndex;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataBox;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;

public class ConstantMdAggregator extends MdAggregator {

	private final StaticContext localContext = StaticContext.EMPTY_CONTEXT;
	private final EnumConstantMdIndex enumConstantMdIndex;

	public ConstantMdAggregator(EnumMdAggregator parent, EnumConstantMdIndex enumConstantMdIndex) {
		super(parent.resolutionContext, ConstantMdDescriptor.T);

		this.enumConstantMdIndex = enumConstantMdIndex;
	}

	public GmEnumConstant getGmEnumConstant() {
		return enumConstantMdIndex.getGmEnumConstant();
	}

	@Override
	protected List<MetaDataBox> acquireFullMetaData(EntityType<? extends MetaData> metaDataType, boolean extended) {
		/* Regarding the key, see comment inside ModelMetaDataResolver#acquireFullMetaData(Class) */
		MetaDataIndexKey mdKey = MetaDataIndexKey.forAll(metaDataType);

		List<MetaDataBox> result = newList();
		result.add(acquireMetaData(enumConstantMdIndex, mdKey, extended));
		result.add(acquireMetaData(enumConstantMdIndex.enumMdIndex.globalEnumConstantMdIndex, mdKey, extended));
		result.add(acquireMetaData(enumConstantMdIndex.enumMdIndex.modelMdIndex.modelConstantMdIndex, mdKey, extended));

		return result;
	}

	@Override
	protected ConstantMdDescriptor extendedFor(QualifiedMetaData qmd, MdIndex ownerIndex) {
		MetaData md = qmd.metaData();
		GmModelElement ownerElement = qmd.ownerElement();

		if (ownerElement == null) {
			return MetaDataWrapper.forConstant(md, null, null, null);

		} else if (ownerElement instanceof GmEnumConstantInfo) {
			GmEnumConstantInfo ownerConstantInfo = (GmEnumConstantInfo) ownerElement;
			return MetaDataWrapper.forConstant(md, ownerConstantInfo.declaringModel(), ownerConstantInfo.declaringTypeInfo(), ownerConstantInfo);

		} else if (ownerElement instanceof GmEnumTypeInfo) {
			GmEnumTypeInfo ownerTypeInfo = (GmEnumTypeInfo) ownerElement;
			return MetaDataWrapper.forConstant(md, ownerTypeInfo.declaringModel(), ownerTypeInfo, null);

		} else if (ownerElement instanceof GmMetaModel) {
			GmMetaModel ownerModel = (GmMetaModel) ownerElement;
			return MetaDataWrapper.forConstant(md, ownerModel, null, null);

		} else {
			throw new IllegalArgumentException("Unexpected owner for enum constant MD: " + ownerElement);
		}
	}

	@Override
	protected StaticContext localContext() {
		return localContext;
	}

}
