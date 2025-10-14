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

import com.braintribe.model.generic.annotation.meta.Aliases;
import com.braintribe.model.generic.annotation.meta.base.BasicRepeatableAggregatorMdaHandler;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.mapping.Alias;

/**
 * Extension of {@link MdaHandler} for annotations which are repeatable.
 * <p>
 * For example {@link Alias} is a repeatable annotation, with the {@link Aliases} as it's aggregator.
 * 
 * @see RepeatableAggregatorMdaHandler
 */
public interface RepeatableMdaHandler<A extends Annotation, RA extends Annotation, M extends MetaData> extends MdaHandler<A, M> {

	/** Returns the {@link MdaHandler} for the aggregator annotation. */
	RepeatableAggregatorMdaHandler<RA, M> aggregatorHandler();

	/**
	 * Special marker for {@link MdaHandler} for aggregator annotations.
	 * <p>
	 * For example, for the annotation {@link Alias}, the corresponding aggregator is {@link Aliases}.
	 * 
	 * <h2>Explanation</h2> When a repeatable annotation is placed on a method, it comes down to if there annotation is applied only once or multiple
	 * times.<br>
	 * If it's once, a regular {@link Annotation} instance is available via reflect (e.g. {@link Class#getAnnotations()}) <br/>
	 * But if it's multiple times, a single instance of the aggregator is available.
	 * <p>
	 * For this reason our {@link MdaRegistry} needs an {@link MdaHandler} for both annotation types, each doing slightly different thing.<br>
	 * But for most cases the handler for the aggregator should be {@link BasicRepeatableAggregatorMdaHandler}, which just extracts the list of
	 * repeatable annotations out of the aggregator and passes the list further.
	 */
	public interface RepeatableAggregatorMdaHandler<RA extends Annotation, M extends MetaData> extends MdaHandler<RA, M> {
		// no extension
	}
}
