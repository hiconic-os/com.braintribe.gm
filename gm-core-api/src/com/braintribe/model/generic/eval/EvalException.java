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

import com.braintribe.model.generic.reflection.GenericModelException;

/**
 * @author peter.gazdik
 */
public class EvalException extends GenericModelException {

	private static final long serialVersionUID = -4346362220050261347L;

	public EvalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EvalException(String message, Throwable cause) {
		super(message, cause);
	}

	public EvalException(String message) {
		super(message);
	}

	public EvalException(Throwable cause) {
		super(cause);
	}

}
