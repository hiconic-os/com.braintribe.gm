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

import java.util.List;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Descriptions;
import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.base.BasicRepeatableAggregatorMdaHandler;
import com.braintribe.model.generic.annotation.meta.base.MdaAnalysisTools;

/**
 * @author peter.gazdik
 */
public class DescriptionMdaHandler implements RepeatableMdaHandler<Description, Descriptions, com.braintribe.model.meta.data.prompt.Description> {

	public static final DescriptionMdaHandler INSTANCE = new DescriptionMdaHandler();

	private final RepeatableAggregatorMdaHandler<Descriptions, com.braintribe.model.meta.data.prompt.Description> repeatableHandler = new BasicRepeatableAggregatorMdaHandler<>(
			Descriptions.class, com.braintribe.model.meta.data.prompt.Description.class, this::buildMdForRepeatable);

	// @formatter:off
	@Override public Class<Description> annotationClass() { return Description.class; }
	@Override public RepeatableAggregatorMdaHandler<Descriptions, com.braintribe.model.meta.data.prompt.Description> aggregatorHandler() { return repeatableHandler; }
	@Override public Class<com.braintribe.model.meta.data.prompt.Description> metaDataClass() { return com.braintribe.model.meta.data.prompt.Description.class; }
	// @formatter:on

	@Override
	public List<com.braintribe.model.meta.data.prompt.Description> buildMdList(Description annotation, MdaAnalysisContext context) {
		return buildMd(context, annotation);
	}

	private List<com.braintribe.model.meta.data.prompt.Description> buildMdForRepeatable(Descriptions descriptions,
			MdaAnalysisContext context) {
		return buildMd(context, descriptions.value());
	}

	public List<com.braintribe.model.meta.data.prompt.Description> buildMd(MdaAnalysisContext context, Description... descriptions) {
		return MdaAnalysisTools.toLsBasedMd(context, this, //
				Description::locale, //
				Description::value, //
				Description::globalId, //
				com.braintribe.model.meta.data.prompt.Description::setDescription, //
				descriptions);
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, com.braintribe.model.meta.data.prompt.Description md) {
		MdaAnalysisTools.buildLsBasedAnnotation(context, md, this, com.braintribe.model.meta.data.prompt.Description::getDescription);
	}

}
