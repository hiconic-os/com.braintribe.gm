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
package com.braintribe.gm.model.reason;

import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.model.generic.reflection.EntityType;

import jsinterop.annotations.JsMethod;

/**
 * An namespace for static functions for the construction of Reasons and related instances
 * 
 * @author Dirk Scheffler
 */
public interface Reasons {

	/**
	 * Starts a fluent {@link ReasonBuilderImpl} that allows to build a reason.
	 */
	@JsMethod(name = "build", namespace = GmCoreApiInteropNamespaces.reason)
	static <R extends Reason> ReasonBuilder<R> build(EntityType<R> reasonType) {
		return new ReasonBuilderImpl<>(reasonType);
	}
	
	static String format(Reason reason) {
		return format(reason, false);
	}
	
	static String format(Reason reason, boolean includeExceptions) {
		StringBuilder builder = new StringBuilder();
		ReasonFormatter.format(builder, reason, 0, includeExceptions);
		
		return builder.toString();
	}
}
