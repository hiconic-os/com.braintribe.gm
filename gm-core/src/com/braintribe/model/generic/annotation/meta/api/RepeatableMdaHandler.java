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
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.mapping.Alias;

/**
 * Extension of {@link MdaHandler} for annotations which are repeatable.
 * <p>
 * For example {@link Alias} is a repeatable annotation, with the {@link Aliases} as it's aggregator.
 */
public interface RepeatableMdaHandler<A extends Annotation, RA extends Annotation, M extends MetaData> extends MdaHandler<A, M> {

	/** Returns the {@link MdaHandler} for the aggregator annotation. */
	RepeatableAggregatorMdaHandler<RA, M> aggregatorHandler();

	/**
	 * Special marker for {@link MdaHandler} for aggregator annotations. For example, for the annotation {@link Alias}, the corresponding aggregator
	 * is {@link Aliases}.
	 */
	public interface RepeatableAggregatorMdaHandler<RA extends Annotation, M extends MetaData> extends MdaHandler<RA, M> {
		// no extension
	}
}
