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
package com.braintribe.model.processing.resource.streaming.cache;

import com.braintribe.model.processing.resource.streaming.ResourceStreamException;

public class CreationTimeoutException extends ResourceStreamException {

	private static final long serialVersionUID = -6546078543155937750L;

	public CreationTimeoutException() {
		super();
	}

	public CreationTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreationTimeoutException(String message) {
		super(message);
	}

	public CreationTimeoutException(Throwable cause) {
		super(cause);
	}

	public static String getBuildVersion() {
		return "$Build_Version$ $Id: CreationTimeoutException.java 86391 2015-05-28 14:25:17Z roman.kurmanowytsch $";
	}
}
