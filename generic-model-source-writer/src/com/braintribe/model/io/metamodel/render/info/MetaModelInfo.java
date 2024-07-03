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
package com.braintribe.model.io.metamodel.render.info;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;

/**
 * Generates and stores all the information needed for rendering of given MetaModel (entities and enums).
 * 
 * @see EntityTypeInfo
 * @see EnumTypeInfo
 */
public class MetaModelInfo {
	private final Map<GmEntityType, EntityTypeInfo> entityTypeInfos = new HashMap<GmEntityType, EntityTypeInfo>();
	private final Map<GmEnumType, EnumTypeInfo> enumTypeInfos = new HashMap<GmEnumType, EnumTypeInfo>();

	// TODO use modelOracle
	public MetaModelInfo(GmMetaModel gmMetaModel) {
		setEntityTypeFullInfo(gmMetaModel);
		setEnumTypeInfos(gmMetaModel);
	}

	public TypeInfo getInfoForType(GmType gmType) {
		if (gmType instanceof GmEntityType) {
			return entityTypeInfos.get(gmType);

		} else if (gmType instanceof GmEnumType) {
			return enumTypeInfos.get(gmType);

		} else {
			return null;
		}
	}

	public EntityTypeInfo getInfoForEntityType(GmEntityType gmEntityType) {
		return entityTypeInfos.get(gmEntityType);
	}

	public EnumTypeInfo getInfoForEnumType(GmEnumType gmEnumType) {
		return enumTypeInfos.get(gmEnumType);
	}

	private void setEntityTypeFullInfo(GmMetaModel gmMetaModel) {
		gmMetaModel.entityTypes().forEach(gmEntityType -> {
			EntityTypeInfo entityInfo = new EntityTypeInfo();
			entityTypeInfos.put(gmEntityType, entityInfo);
			loadPackageAndClassInfo(gmEntityType, entityInfo);
			entityInfo.superTypes = getSuperTypes(gmEntityType);
		});
	}

	private void setEnumTypeInfos(GmMetaModel gmMetaModel) {
		gmMetaModel.enumTypes().forEach(gmEnumType -> {
			EnumTypeInfo typeInfo = new EnumTypeInfo();
			loadPackageAndClassInfo(gmEnumType, typeInfo);

			enumTypeInfos.put(gmEnumType, typeInfo);
		});
	}

	private void loadPackageAndClassInfo(GmType gmType, TypeInfo typeInfo) {
		typeInfo.packageName = getPackageName(gmType);
		typeInfo.simpleName = getSimpleClassName(gmType);
		typeInfo.fullName = gmType.getTypeSignature();
	}

	private String getPackageName(GmType gmType) {
		String className = gmType.getTypeSignature();
		int lastDotPos = className.lastIndexOf(".");

		if (lastDotPos < 0) {
			return "";
		}

		return className.substring(0, lastDotPos);
	}

	private String getSimpleClassName(GmType gmType) {
		String className = gmType.getTypeSignature();
		int lastDotPos = className.lastIndexOf(".");

		if (lastDotPos < 0) {
			return className;
		}

		return className.substring(lastDotPos + 1);
	}

	@SuppressWarnings("unchecked")
	private List<GmEntityType> getSuperTypes(GmEntityType gmEntityType) {
		List<GmEntityType> superTypes = gmEntityType.getSuperTypes();
		return superTypes != null ? superTypes : Collections.EMPTY_LIST;
	}
}
