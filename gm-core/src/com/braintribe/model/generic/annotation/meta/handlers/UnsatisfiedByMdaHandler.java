// ============================================================================
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
package com.braintribe.model.generic.annotation.meta.handlers;

import static com.braintribe.model.generic.annotation.meta.base.MdaAnalysisTools.newMd;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.model.generic.annotation.meta.UnsatisfiedBy;
import com.braintribe.model.generic.annotation.meta.UnsatisfiedBys;
import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.ClassReference;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.SingleAnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.base.BasicRepeatableAggregatorMdaHandler;
import com.braintribe.model.meta.GmEntityType;

public class UnsatisfiedByMdaHandler
		implements RepeatableMdaHandler<UnsatisfiedBy, UnsatisfiedBys, com.braintribe.model.meta.data.mapping.UnsatisfiedBy> {

	public static final UnsatisfiedByMdaHandler INSTANCE = new UnsatisfiedByMdaHandler();

	private final RepeatableAggregatorMdaHandler<UnsatisfiedBys, com.braintribe.model.meta.data.mapping.UnsatisfiedBy> aggregatorHandler = new BasicRepeatableAggregatorMdaHandler<>(
			UnsatisfiedBys.class, com.braintribe.model.meta.data.mapping.UnsatisfiedBy.class, this::buildMdListForRepeatable);

	// @formatter:off
	@Override public Class<UnsatisfiedBy> annotationClass() { return UnsatisfiedBy.class; }
	@Override public RepeatableAggregatorMdaHandler<UnsatisfiedBys, com.braintribe.model.meta.data.mapping.UnsatisfiedBy> aggregatorHandler() { return aggregatorHandler; }
	@Override public Class<com.braintribe.model.meta.data.mapping.UnsatisfiedBy> metaDataClass() { return com.braintribe.model.meta.data.mapping.UnsatisfiedBy.class; }
	// @formatter:on

	@Override
	public List<com.braintribe.model.meta.data.mapping.UnsatisfiedBy> buildMdList(UnsatisfiedBy annotation, MdaAnalysisContext context) {
		return buildMetaDataFor(context, annotation);
	}

	private List<com.braintribe.model.meta.data.mapping.UnsatisfiedBy> buildMdListForRepeatable(UnsatisfiedBys annotations,
			MdaAnalysisContext context) {
		return buildMetaDataFor(context, annotations.value());
	}

	private static List<com.braintribe.model.meta.data.mapping.UnsatisfiedBy> buildMetaDataFor(MdaAnalysisContext context,
			UnsatisfiedBy... annotations) {
		List<com.braintribe.model.meta.data.mapping.UnsatisfiedBy> result = newList();

		int i = 0;
		for (UnsatisfiedBy potentialReason : annotations)
			result.add(toPotentialReasonMd(context, potentialReason, i++));

		return result;
	}

	private static com.braintribe.model.meta.data.mapping.UnsatisfiedBy toPotentialReasonMd(MdaAnalysisContext context,
			UnsatisfiedBy annotation, int i) {
		String globalId = annotation.globalId();

		com.braintribe.model.meta.data.mapping.UnsatisfiedBy result = newMd(context, com.braintribe.model.meta.data.mapping.UnsatisfiedBy.T,
				globalId, i);
		GmEntityType gmType = (GmEntityType) context.getGmType(annotation.value());
		result.setReasonType(gmType);

		return result;
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, com.braintribe.model.meta.data.mapping.UnsatisfiedBy md) {
		SingleAnnotationDescriptor result = context.newDescriptor(UnsatisfiedBy.class);
		result.addAnnotationValue("type", new ClassReference(md.getReasonType().getTypeSignature()));
		context.setCurrentDescriptorMulti(result, UnsatisfiedBys.class);
	}

}
