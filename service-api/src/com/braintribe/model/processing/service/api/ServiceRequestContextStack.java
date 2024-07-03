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
package com.braintribe.model.processing.service.api;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.utils.collection.api.MinimalStack;

public interface ServiceRequestContextStack extends MinimalStack<AttributeContext> {

	/**
	 * <p>
	 * Pushes the given {@code attributeContext} to this
	 * {@code ServiceRequestContextStack}.
	 * 
	 * <p>
	 * The pushed {@link AttributeContext} is subsequently available through {@link #get()} calls.
	 * 
	 * <p>
	 * In order to release any resources associated with the pushed {@link AttributeContext},
	 * callers must ensure that {@link #pop()} is invoked once the scope for which
	 * the {@link AttributeContext} was created is completed.
	 * 
	 * @param attributeContext
	 *            The {@link AttributeContext} to be pushed.
	 * @deprecated use {@link #push(AttributeContext)} instead
	 */
	@Deprecated
	default void pushDirect(AttributeContext attributeContext) { push(attributeContext); }
}