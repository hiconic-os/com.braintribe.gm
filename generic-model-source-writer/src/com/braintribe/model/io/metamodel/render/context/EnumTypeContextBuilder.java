// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.io.metamodel.render.context;

import static com.braintribe.model.io.metamodel.render.context.SourceWriterTools.getGlobalIdAnnotationSource;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.lang.annotation.Annotation;
import java.util.List;

import com.braintribe.model.generic.annotation.GlobalId;
import com.braintribe.model.generic.annotation.meta.api.synthesis.AnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.synthesis.MdaSynthesis;
import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;
import com.braintribe.model.io.metamodel.render.info.MetaModelInfo;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;

public class EnumTypeContextBuilder {

	private final GmEnumType gmEnumType;

	private final ImportManager im;
	private final EnumTypeContext result = new EnumTypeContext();

	public EnumTypeContextBuilder(GmEnumType gmEnumType, MetaModelInfo metaModelInfo) {
		this.gmEnumType = gmEnumType;
		this.result.typeInfo = metaModelInfo.getInfoForEnumType(gmEnumType);
		this.result.importManager = im = new ImportManager(result.typeInfo.packageName);
	}

	public EnumTypeContext build() {
		noteOwnTypeToNotConflictImports();
		importTypesForTypeLiteral();
		setConstants();
		setAnnotations();

		return result;
	}

	private void noteOwnTypeToNotConflictImports() {
		im.useType(gmEnumType.getTypeSignature());
	}

	private void importTypesForTypeLiteral() {
		im.useType(EnumBase.class);
		im.useType(EnumType.class);
		im.useType(EnumTypes.class);
	}

	private void setConstants() {
		List<GmEnumConstant> gmConstants = gmEnumType.getConstants();

		List<ConstantDescriptor> constants = newList();

		for (GmEnumConstant gmConstant : gmConstants) {
			List<String> annotations = prepareConstantAnnotations(gmConstant);
			constants.add(new ConstantDescriptor(gmConstant, annotations));
		}

		result.constants = constants;
	}

	private List<String> prepareConstantAnnotations(GmEnumConstant gmConstant) {
		List<String> result = newList();

		if (SourceWriterTools.elmentNeedsGlobalId(gmConstant)) {
			addAnnotationImport(GlobalId.class);
			result.add(getGlobalIdAnnotationSource(gmConstant.getGlobalId()));
		}

		MdaSynthesis.synthesizeMetaDataAnnotations(gmConstant).forEach((AnnotationDescriptor ad) -> {
			addAnnotationImport(ad.getAnnotationClass());
			ad.withSourceCode(result::add);
		});

		return result;
	}

	private void setAnnotations() {
		List<String> annotations = newList();

		if (SourceWriterTools.elmentNeedsGlobalId(gmEnumType)) {
			addAnnotationImport(GlobalId.class);
			annotations.add(getGlobalIdAnnotationSource(gmEnumType.getGlobalId()));
		}

		for (AnnotationDescriptor annotationDescriptor : MdaSynthesis.synthesizeMetaDataAnnotations(gmEnumType)) {
			addAnnotationImport(annotationDescriptor.getAnnotationClass());
			annotationDescriptor.withSourceCode(annotations::add);
		}

		result.annotations = annotations;
	}

	private void addAnnotationImport(Class<? extends Annotation> annotationClass) {
		im.useType(annotationClass.getName());
	}

}
