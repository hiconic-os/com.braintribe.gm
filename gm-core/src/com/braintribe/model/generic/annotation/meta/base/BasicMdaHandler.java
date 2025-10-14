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

import java.lang.annotation.Annotation;
import java.util.function.Function;

import com.braintribe.model.generic.annotation.meta.api.MdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.SingleAnnotationDescriptor;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.Predicate;

/**
 * @author peter.gazdik
 */
public class BasicMdaHandler<A extends Annotation, M extends MetaData> implements MdaHandler<A, M> {

	private final Class<A> annotationClass;
	private final Function<A, String> globalIdResolver;
	private final AnnotationToMetaDataPropertyCopier<A, M> a2mPropertyCopier;
	private final MetaDataToDescriptorPropertyCopier<M> m2dPropertyCopier;
	private Class<M> metaDataClass;

	/** Constructor for {@link Predicate} metadata, where having properties makes no sense (think about it). */
	public BasicMdaHandler(Class<A> annotationClass, Class<M> metaDataClass, Function<A, String> globalIdResolver) {
		this(annotationClass, metaDataClass, globalIdResolver, (c, a, m) -> { /* NO OP */ }, (c, a, m) -> { /* NO OP */ });
	}

	public BasicMdaHandler(Class<A> annotationClass, Class<M> metaDataClass, Function<A, String> globalIdResolver, //
			AnnotationToMetaDataPropertyCopier<A, M> a2mPropertyCopier, MetaDataToDescriptorPropertyCopier<M> m2dPropertyCopier) {

		this.annotationClass = annotationClass;
		this.metaDataClass = metaDataClass;
		this.globalIdResolver = globalIdResolver;
		this.a2mPropertyCopier = a2mPropertyCopier;
		this.m2dPropertyCopier = m2dPropertyCopier;
	}

	@FunctionalInterface
	public static interface AnnotationToMetaDataPropertyCopier<A extends Annotation, M extends MetaData> {
		void copyProperties(MdaAnalysisContext context, A annotation, M metaData);
	}

	@FunctionalInterface
	public static interface MetaDataToDescriptorPropertyCopier<M extends MetaData> {
		void copyProperties(MdaSynthesisContext context, SingleAnnotationDescriptor annotationDescriptor, M metaData);
	}

	@Override
	public Class<A> annotationClass() {
		return annotationClass;
	}

	@Override
	public Class<M> metaDataClass() {
		return metaDataClass;
	}

	@Override
	public M buildMd(A annotation, MdaAnalysisContext context) {
		M m = newMd(context, metaDataType(), globalIdResolver.apply(annotation));
		a2mPropertyCopier.copyProperties(context, annotation, m);
		return m;
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, M metaData) {
		SingleAnnotationDescriptor annotationDescriptor = context.newDescriptor(annotationClass);
		context.setCurrentDescriptor(annotationDescriptor);

		m2dPropertyCopier.copyProperties(context, annotationDescriptor, metaData);
	}

}
