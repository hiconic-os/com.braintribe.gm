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
package com.braintribe.model.processing.session.api.resource;

import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.ResourceSource;
import com.braintribe.model.resourceapi.persistence.DeletionScope;

public interface ResourceDeleteBuilder {

	/**
	 * <p>
	 * Sets the use case under which the {@link Resource} binary data is to be deleted.
	 * 
	 * @param useCase
	 *            the use case under which the {@link Resource} binary data is to be deleted.
	 *            
	 * @deprecated This setting is currently being ignored because the correct useCase should be found on the
	 *             {@link ResourceSource} of the respective resource
	 */
	@Deprecated
	ResourceDeleteBuilder useCase(String useCase);
	
	/**
	 * @see DeletionScope
	 */
	default ResourceDeleteBuilder scope(DeletionScope scope) {
		throw new UnsupportedOperationException();
	}

	/**
	 * <p>
	 * Deletes the binary data backed by the provided {@link Resource}.
	 * 
	 * @throws java.io.UncheckedIOException
	 *             If the IO operation fails.
	 */
	void delete();

}
