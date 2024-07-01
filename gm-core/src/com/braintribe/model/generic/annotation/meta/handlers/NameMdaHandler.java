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

import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.annotation.meta.Names;
import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.base.BasicRepeatableAggregatorMdaHandler;
import com.braintribe.model.generic.annotation.meta.base.MdaAnalysisTools;

/**
 * @author peter.gazdik
 */
public class NameMdaHandler implements RepeatableMdaHandler<Name, Names, com.braintribe.model.meta.data.prompt.Name> {

	public static final NameMdaHandler INSTANCE = new NameMdaHandler();

	private final RepeatableAggregatorMdaHandler<Names, com.braintribe.model.meta.data.prompt.Name> repeatableHandler = new BasicRepeatableAggregatorMdaHandler<>(
			Names.class, com.braintribe.model.meta.data.prompt.Name.class, this::buildMdListForRepeatable);

	// @formatter:off
	@Override public Class<Name> annotationClass() { return Name.class; }
	@Override public RepeatableAggregatorMdaHandler<Names, com.braintribe.model.meta.data.prompt.Name> aggregatorHandler() { return repeatableHandler; }
	@Override public Class<com.braintribe.model.meta.data.prompt.Name> metaDataClass() { return com.braintribe.model.meta.data.prompt.Name.class; }
	// @formatter:on

	@Override
	public List<com.braintribe.model.meta.data.prompt.Name> buildMdList(Name annotation, MdaAnalysisContext context) {
		return buildMd(context, annotation);
	}

	private List<com.braintribe.model.meta.data.prompt.Name> buildMdListForRepeatable(Names names, MdaAnalysisContext context) {
		return buildMd(context, names.value());
	}

	public List<com.braintribe.model.meta.data.prompt.Name> buildMd(MdaAnalysisContext context, Name... names) {
		return MdaAnalysisTools.toLsBasedMd(context, this, //
				Name::locale, //
				Name::value, //
				Name::globalId, //
				com.braintribe.model.meta.data.prompt.Name::setName, //
				names);
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, com.braintribe.model.meta.data.prompt.Name md) {
		MdaAnalysisTools.buildLsBasedAnnotation(context, md, this, com.braintribe.model.meta.data.prompt.Name::getName);
	}

}
