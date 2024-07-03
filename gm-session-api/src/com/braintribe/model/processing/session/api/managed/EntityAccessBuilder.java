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
package com.braintribe.model.processing.session.api.managed;

import com.braintribe.model.accessapi.ReferencesResponse;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.processing.async.api.AsyncCallback;

public interface EntityAccessBuilder<T extends GenericEntity> {

	/**
	 * Returns the specified entity or <code>null</code>, if it doesn't exist.
	 */
	T find() throws GmSessionException;
	void find(AsyncCallback<T> asyncCallback);

	/**
	 * {@link #find() Finds} the specified entity and throws an exception, if it doesn't exist.
	 * 
	 * @throws NotFoundException
	 *             if the specified entity doesn't exist.
	 */
	T require() throws GmSessionException, NotFoundException;
	void require(AsyncCallback<T> asyncCallback);

	ReferencesResponse references() throws GmSessionException;
	void references(AsyncCallback<ReferencesResponse> asyncCallback);
}
