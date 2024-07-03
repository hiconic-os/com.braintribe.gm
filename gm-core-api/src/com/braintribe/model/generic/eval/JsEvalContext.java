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
package com.braintribe.model.generic.eval;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.processing.async.api.JsPromise;

import jsinterop.annotations.JsType;

/**
 * This is only used in the GWT codebase. It is defined here, because the TypeScript declarations refer to this type (the eval method returns
 * JsEvalContext). So it needs to be somewhere visible from each model, but cannot be even deeper (say jsinterop-base), because this extends
 * EvalContext which is defined here.
 * 
 * @author peter.gazdik
 */
@JsType(namespace = GmCoreApiInteropNamespaces.eval)
public interface JsEvalContext<R> extends EvalContext<R> {

	JsPromise<R> andGet();

	JsPromise<Maybe<R>> andGetReasoned();

}