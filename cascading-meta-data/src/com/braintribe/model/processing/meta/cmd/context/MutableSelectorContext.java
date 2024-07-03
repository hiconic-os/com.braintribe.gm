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
package com.braintribe.model.processing.meta.cmd.context;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.meta.cmd.builders.MdResolver;

/**
 * @see SelectorContext
 */
public interface MutableSelectorContext extends ExtendedSelectorContext {

	/**
	 * Puts new value for given aspect into the {@link SelectorContext}.
	 * <p>
	 * Value must not be {@code null} Implementation should throw an exception if caller tries to put a {@code null} in the context.
	 */
	<T, A extends SelectorContextAspect<? super T>> void put(Class<A> aspect, T value);

	/** Sets the {@link MdResolver#ignoreSelectors()} flag on the context. */
	void ignoreSelectors(EntityType<?>... exceptions);

	MutableSelectorContext copy();

}
