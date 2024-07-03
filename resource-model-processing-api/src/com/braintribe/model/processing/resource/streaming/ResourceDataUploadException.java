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

/**
 * @deprecated Not supported as of tribefire 2.0
 */
@Deprecated
public class ResourceDataUploadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8167437523818919498L;

	public ResourceDataUploadException() {
		super();
	}

	public ResourceDataUploadException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceDataUploadException(String message) {
		super(message);
	}

	public ResourceDataUploadException(Throwable cause) {
		super(cause);
	}

}
