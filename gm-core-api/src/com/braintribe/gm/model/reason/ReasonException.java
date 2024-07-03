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

import jsinterop.annotations.JsType;

/**
 * A ReasonException is thrown in cases a Reason is not handled which occurs when one accesses an unsatisfied Maybe via {@link Maybe#get()}. 
 * This turns the potential of a controlled handling of unsatisfied values into an exception situation which is not meant to be handled.
 * 
 * @author Dirk Scheffler
 */
@JsType(namespace = GmCoreApiInteropNamespaces.reason)
public class ReasonException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final Reason reason;

	/**
	 * Constructs a ReasonException with no stacktrace.
	 * 
	 * @param reason The {@link Reason} to be transported
	 */
	public ReasonException(Reason reason) {
		super(reason.stringify());
		this.reason = reason;
	}
	
	public Reason getReason() {
		return reason;
	}
}
