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
package com.braintribe.model.generic.enhance;

import com.braintribe.model.generic.annotation.GmSystemInterface;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.session.SessionAttachable;

/**
 * Enhanced entity is the standard entity implementation we use in GM, and it supports {@link PropertyAccessInterceptor}
 * driven cross-cutting concerns. This enables features like manipulation-tracking, lazy-loading, or ensuring reading a
 * collection property never results in <tt>null</tt> being returned. <br>
 * The counterpart of an enhanced entity is called a plain entity, which is implemented as a POJO, which provides a
 * minor advantage in smaller memory impact.
 */
@SuppressWarnings("deprecation")
@GmSystemInterface
public interface EnhancedEntity extends SessionAttachable {

	/**
	 * Returns the flags of given instance.
	 * 
	 * @see EntityFlags
	 */
	int flags();

	void assignFlags(int flags);

	/** Adds a {@link PropertyAccessInterceptor} (PAI) to the beginning of the PAI chain for this entity. */
	void pushPai(PropertyAccessInterceptor pai);

	/**
	 * Removes and returns the first {@link PropertyAccessInterceptor} (PAI) from the entity's PAI chain. This should
	 * only be called to remove a PAI which was previously pushed by the same client code.
	 */
	PropertyAccessInterceptor popPai();

	/**
	 * Set's this entity's {@link PropertyAccessInterceptor} (PAI) to the given value. Any previously set PAI is lost.
	 */
	void assignPai(PropertyAccessInterceptor pai);
}
