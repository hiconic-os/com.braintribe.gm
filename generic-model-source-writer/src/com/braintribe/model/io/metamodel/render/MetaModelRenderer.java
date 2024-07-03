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
package com.braintribe.model.io.metamodel.render;

import com.braintribe.model.io.metamodel.MetaModelSourceDescriptor;
import com.braintribe.model.io.metamodel.render.context.EntityTypeContext;
import com.braintribe.model.io.metamodel.render.context.EntityTypeContextBuilder;
import com.braintribe.model.io.metamodel.render.context.EnumTypeContext;
import com.braintribe.model.io.metamodel.render.context.EnumTypeContextBuilder;
import com.braintribe.model.io.metamodel.render.context.TypeSignatureResolver;
import com.braintribe.model.io.metamodel.render.info.MetaModelInfo;
import com.braintribe.model.io.metamodel.render.info.TypeInfo;
import com.braintribe.model.io.metamodel.render.render.EntityRenderer;
import com.braintribe.model.io.metamodel.render.render.EnumRenderer;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmType;

/**
 * For given GmType (Entity/Enum) creates source code and information about the name of the file which should be created as source file (This
 * information is encapsulated in a Class MetaModelSourceDescriptor).
 */
public class MetaModelRenderer {

	private final SourceWriterContext context;
	private final MetaModelInfo metaModelInfo;
	private final TypeSignatureResolver typeSignatureResolver;

	public MetaModelRenderer(SourceWriterContext context) {
		this.context = context;
		this.metaModelInfo = new MetaModelInfo(context.modelOracle.getGmMetaModel());
		this.typeSignatureResolver = new TypeSignatureResolver();
	}

	public MetaModelSourceDescriptor renderEntityType(GmEntityType gmEntityType) {
		if (!shouldRenderType(gmEntityType))
			return null;

		MetaModelSourceDescriptor result = createNewDescriptorForType(gmEntityType);

		EntityTypeContext context = provideEntityTypeContext(gmEntityType, metaModelInfo);
		result.sourceCode = new EntityRenderer(context).render();

		return result;
	}

	private EntityTypeContext provideEntityTypeContext(GmEntityType gmEntityType, MetaModelInfo metaModelInfo) {
		return new EntityTypeContextBuilder(context, typeSignatureResolver, gmEntityType, metaModelInfo).build();
	}

	public MetaModelSourceDescriptor renderEnumType(GmEnumType gmEnumType) {
		MetaModelSourceDescriptor result = createNewDescriptorForType(gmEnumType);

		EnumTypeContext context = provideEnumTypeContext(gmEnumType, metaModelInfo);
		result.sourceCode = new EnumRenderer(context).render();

		return result;
	}

	private EnumTypeContext provideEnumTypeContext(GmEnumType gmEnumType, MetaModelInfo metaModelInfo) {
		return new EnumTypeContextBuilder(gmEnumType, metaModelInfo).build();
	}

	private boolean shouldRenderType(GmType gmType) {
		return context.shouldWriteSourcesForExistingClasses || !existsClass(gmType.getTypeSignature());
	}

	private boolean existsClass(String className) {
		try {
			Class.forName(className);
			return true;

		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/** Should only be called with GmEnumType or GmEntityType, which are retrievable through metaModelInfo.getInfoForType(); */
	private MetaModelSourceDescriptor createNewDescriptorForType(GmType gmType) {
		MetaModelSourceDescriptor result = new MetaModelSourceDescriptor();

		TypeInfo entityInfo = metaModelInfo.getInfoForType(gmType);
		result.sourceRelativePath = getSourceRelativePath(entityInfo);

		return result;
	}

	private String getSourceRelativePath(TypeInfo entityInfo) {
		String pathToPackageFolder = entityInfo.packageName.replaceAll("\\.", "/");

		return pathToPackageFolder + "/" + entityInfo.simpleName + ".java";
	}

}
