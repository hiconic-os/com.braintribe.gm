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
package com.braintribe.model.generic.annotation.meta.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.Collections;
import java.util.List;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.annotation.meta.Alias;
import com.braintribe.model.generic.annotation.meta.Aliases;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;

/**
 * MdaHandler is responsible for converting annotations of type A into one or more MD (and back, when generating bytecode or source code).
 * <p>
 * IMPLEMENTATION NOTE: From the outside (JTA) only the {@link #buildMdList} is called, but it has a default implementation that delegates to
 * {@link #buildMd} so overriding either of these two methods is fine.
 * 
 * <h2>Registering custom handlers</h2>
 * 
 * Custom handlers can be registered by adding a text file on path <b><i>{@value CustomMdaHandlerLoader#MDA_LOCATION}</i></b> to your classpath.
 * <p>
 * Ideally put this file in the model that declares both the Annotation type and MetaData type you are declaring.
 * <p>
 * The file contains one line per annotation, each line consisting of comma separated values. The first two values are fully qualified class names of
 * the annotation and the meta-data type, respectively, optionally followed by a sequence of pairs [annotation attribute name, property name]. For
 * example:
 * 
 * <pre>
 * my.pckg.anno.@MyPredicateMdAnnotation,my.pack.model.MyPredicateMdType
 * my.pckg.anno.@MyMdAnnotation,my.pack.model.MyMdType,value,name,optionalValue,optionalValue
 * </pre>
 * 
 * <h3>Special considerations</h3>
 * 
 * <h4>GlobalId</h4>
 * 
 * Every annotation must have an attribute globaId, which ideally defaults to an empty string - see e.g. {@link Mandatory#globalId()}.<br>
 * Consider this legacy, we will remove this constraints once we get rid of Cortex.
 * 
 * <h4>Common properties: "important" and "inherited"</h4>
 * 
 * These properties don't require a mapping entry, if the annotation has an attribute {@code boolean important()} or {@code boolean inherited()}, it
 * is automatically mapped to the corresponding MD property.
 * 
 * <h4>Repeatable annotations</h4>
 * 
 * {@link Repeatable} annotations are supported automatically by the framework and no extra mapping entry is required for the "aggregator" annotation.
 * For example, we would only have an entry for {@link Alias}, but none for {@link Aliases}.
 * <p>
 * This, however, results in multiple MD instances being added for the corresponding element. If we however wanted to merge multiple annotations into
 * a single MD, like we are doing for {@link Name} and {@link Description} - one annotation for one locale - this is currently not supported.
 * 
 * @see RepeatableMdaHandler
 * 
 * @author peter.gazdik
 */
public interface MdaHandler<A extends Annotation, M extends MetaData> {

	Class<A> annotationClass();

	Class<M> metaDataClass();

	default EntityType<M> metaDataType() {
		return GMF.getTypeReflection().getEntityType(metaDataClass());
	}

	// Analyzer

	default List<M> buildMdList(A annotation, MdaAnalysisContext context) {
		return Collections.singletonList(buildMd(annotation, context));
	}

	@SuppressWarnings("unused")
	default M buildMd(A annotation, MdaAnalysisContext context) {
		throw new UnsupportedOperationException("Implementation class must either implement this method or the one that returns a list");
	}

	// Synthesizer

	void buildAnnotation(MdaSynthesisContext context, M metaData);

}
