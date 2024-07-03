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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.api.ResourceReadAccess;

/**
 * Grants access to Resources based on the ResourceModel it support building URLs to existing resources, streaming
 * existing resources and creating new resources
 * 
 * @author dirk.scheffler
 *
 */
public interface ResourceAccess extends ResourceReadAccess {

	ResourceUrlBuilder url(Resource resource);

	ResourceCreateBuilder create();

	ResourceRetrieveBuilder retrieve(Resource resource);
	
	ResourceUpdateBuilder update(Resource resource);

	ResourceDeleteBuilder delete(Resource resource);

	@Override
	default InputStream openStream(Resource resource) throws IOException {
		return retrieve(resource).stream();
	}

	@Override
	default void writeToStream(Resource resource, OutputStream outputStream) throws IOException {
		retrieve(resource).stream(outputStream);
	}

}
