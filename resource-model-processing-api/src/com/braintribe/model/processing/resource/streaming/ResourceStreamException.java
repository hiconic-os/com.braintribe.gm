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
package com.braintribe.model.processing.resource.streaming;

public class ResourceStreamException extends Exception {

	private static final long serialVersionUID = -7009670749804311112L;

	public ResourceStreamException() {
	}

	public ResourceStreamException(String message) {
		super(message);
	}

	public ResourceStreamException(Throwable cause) {
		super(cause);
	}

	public ResourceStreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public static String getBuildVersion() {
		return "$Build_Version$ $Id: ResourceStreamException.java 86391 2015-05-28 14:25:17Z roman.kurmanowytsch $";
	}
}
