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

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.processing.meta.cmd.extended.ConstantMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EntityMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EntityRelatedMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EnumMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EnumRelatedMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.MdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.ModelMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.PropertyMdDescriptor;

/**
 * @author peter.gazdik
 */
public class MetaDataWrapper {

	static MdDescriptor forDefault(EntityType<? extends MdDescriptor> descriptorType, MetaData md) {
		MdDescriptor result = descriptorType.create();
		result.setResolvedValue(md);
		result.setResolvedAsDefault(true);

		return result;
	}

	static PropertyMdDescriptor forProperty(MetaData md, GmEntityType resolvedForType, GmMetaModel metaModel, GmEntityTypeInfo ownerEntityTypeInfo,
			GmPropertyInfo ownerPropertyInfo) {

		PropertyMdDescriptor result = forEntityRelated(PropertyMdDescriptor.T, md, resolvedForType, metaModel, ownerEntityTypeInfo);
		result.setOwnerPropertyInfo(ownerPropertyInfo);

		return result;
	}

	static EntityMdDescriptor forEntityType(MetaData md, GmEntityType resolvedForType, GmMetaModel metaModel, GmEntityTypeInfo ownerTypeInfo) {
		return forEntityRelated(EntityMdDescriptor.T, md, resolvedForType, metaModel, ownerTypeInfo);
	}

	private static <D extends EntityRelatedMdDescriptor> D forEntityRelated(EntityType<D> descriptorType, MetaData md, GmEntityType resolvedForType,
			GmMetaModel metaModel, GmEntityTypeInfo ownerTypeInfo) {

		D result = newInstance(descriptorType, md, metaModel);
		result.setResolvedForType(resolvedForType);
		result.setOwnerTypeInfo(ownerTypeInfo);
		result.setInherited(md.getInherited());

		return result;
	}

	static ConstantMdDescriptor forConstant(MetaData md, GmMetaModel ownerModel, GmEnumTypeInfo ownerTypeInfo, GmEnumConstantInfo ownerConstantInfo) {
		ConstantMdDescriptor result = forEnumRelated(ConstantMdDescriptor.T, md, ownerModel, ownerTypeInfo);
		result.setOwnerConstantInfo(ownerConstantInfo);
		return result;
	}

	static EnumMdDescriptor forEnumType(MetaData md, GmMetaModel ownerModel, GmEnumTypeInfo ownerTypeInfo) {
		return forEnumRelated(EnumMdDescriptor.T, md, ownerModel, ownerTypeInfo);
	}

	private static <D extends EnumRelatedMdDescriptor> D forEnumRelated(EntityType<D> descriptorType, MetaData md, GmMetaModel ownerModel,
			GmEnumTypeInfo ownerTypeInfo) {
		D result = newInstance(descriptorType, md, ownerModel);
		result.setOwnerTypeInfo(ownerTypeInfo);
		result.setInherited(md.getInherited());

		return result;
	}

	static ModelMdDescriptor forModel(MetaData md, GmMetaModel ownerModel) {
		return newInstance(ModelMdDescriptor.T, md, ownerModel);
	}

	private static <D extends MdDescriptor> D newInstance(EntityType<D> descriptorType, MetaData md, GmMetaModel ownerModel) {
		D result = descriptorType.create();
		result.setResolvedValue(md);
		result.setResolvedAsDefault(false);
		result.setOwnerModel(ownerModel);

		return result;
	}

}
