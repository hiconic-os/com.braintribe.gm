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
package com.braintribe.model.generic.annotation.meta.base;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.BiFunction;

import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler.RepeatableAggregatorMdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.meta.data.MetaData;

public class BasicRepeatableAggregatorMdaHandler<RA extends Annotation, M extends MetaData> implements RepeatableAggregatorMdaHandler<RA, M> {

	private final Class<RA> annotationClass;
	private final Class<M> metaDataClass;
	private final BiFunction<RA, MdaAnalysisContext, List<M>> mdListBuilder;

	public BasicRepeatableAggregatorMdaHandler( //
			Class<RA> annotationClass, //
			Class<M> metaDataClass, //
			BiFunction<RA, MdaAnalysisContext, List<M>> mdListBuilder) {

		this.annotationClass = annotationClass;
		this.metaDataClass = metaDataClass;
		this.mdListBuilder = mdListBuilder;
	}

	@Override
	public Class<RA> annotationClass() {
		return annotationClass;
	}

	@Override
	public Class<M> metaDataClass() {
		return metaDataClass;
	}

	@Override
	public List<M> buildMdList(RA annotation, MdaAnalysisContext context) {
		return mdListBuilder.apply(annotation, context);
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, M metaData) {
		throw new UnsupportedOperationException("This method should not be invoked for the repeatabe-aggregator type: " + annotationClass.getName());
	}

}
