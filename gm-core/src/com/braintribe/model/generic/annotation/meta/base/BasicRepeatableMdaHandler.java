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

import static com.braintribe.model.generic.annotation.meta.base.MdaAnalysisTools.newMd;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Function;

import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.SingleAnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.base.BasicMdaHandler.AnnotationToMetaDataPropertyCopier;
import com.braintribe.model.generic.annotation.meta.base.BasicMdaHandler.MetaDataToDescriptorPropertyCopier;
import com.braintribe.model.meta.data.MetaData;

/**
 * @author peter.gazdik
 */
public class BasicRepeatableMdaHandler<A extends Annotation, RAA extends Annotation, M extends MetaData> implements RepeatableMdaHandler<A, RAA, M> {

	private final Class<A> annotationClass;
	private final RepeatableAggregatorMdaHandler<RAA, M> aggregatorHandler;
	private final Class<M> metaDataClass;

	private final Function<A, String> globalIdResolver;
	private final Function<RAA, A[]> repeatableExtractor;

	private final AnnotationToMetaDataPropertyCopier<A, M> a2mPropertyCopier;
	private final MetaDataToDescriptorPropertyCopier<M> m2dPropertyCopier;
	private final Class<RAA> aggregatorAnnotationClass;

	public BasicRepeatableMdaHandler(//
			Class<A> annotationClass, //
			Class<RAA> aggregatorAnnotationClass, //
			Class<M> metaDataClass, //
			Function<A, String> globalIdResolver, //
			Function<RAA, A[]> repeatableExtractor, //
			AnnotationToMetaDataPropertyCopier<A, M> a2mPropertyCopier, //
			MetaDataToDescriptorPropertyCopier<M> m2dPropertyCopier //
	) {
		this.annotationClass = annotationClass;
		this.aggregatorAnnotationClass = aggregatorAnnotationClass;
		this.metaDataClass = metaDataClass;
		this.globalIdResolver = globalIdResolver;
		this.repeatableExtractor = repeatableExtractor;

		this.a2mPropertyCopier = a2mPropertyCopier;
		this.m2dPropertyCopier = m2dPropertyCopier //
		;
		this.aggregatorHandler = new BasicRepeatableAggregatorMdaHandler<>(aggregatorAnnotationClass, metaDataClass, this::buildMdListForRepeatable);
	}

	// @formatter:off
	@Override public Class<A> annotationClass() { return annotationClass; }
	@Override public RepeatableAggregatorMdaHandler<RAA, M> aggregatorHandler() { return aggregatorHandler; }
	@Override public Class<M> metaDataClass() { return metaDataClass; }
	// @formatter:on

	@Override
	public List<M> buildMdList(A annotation, MdaAnalysisContext context) {
		return buildMetaDataFor(context, annotation);
	}

	private List<M> buildMdListForRepeatable(RAA repeatableAnno, MdaAnalysisContext context) {
		return buildMetaDataFor(context, repeatableExtractor.apply(repeatableAnno));
	}

	private List<M> buildMetaDataFor(MdaAnalysisContext context, A... annos) {
		List<M> result = newList();

		int i = 0;
		for (A anno : annos)
			result.add(toMd(context, anno, i++));

		return result;
	}

	private M toMd(MdaAnalysisContext context, A anno, int i) {
		String globalId = globalIdResolver.apply(anno);

		M result = newMd(context, metaDataType(), globalId, i);
		a2mPropertyCopier.copyProperties(context, anno, result);

		return result;
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, M md) {
		SingleAnnotationDescriptor result = context.newDescriptor(annotationClass);
		m2dPropertyCopier.copyProperties(context, result, md);

		context.setCurrentDescriptorMulti(result, aggregatorAnnotationClass);
	}

}
