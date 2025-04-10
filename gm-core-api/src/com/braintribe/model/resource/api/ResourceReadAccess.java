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
package com.braintribe.model.resource.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.braintribe.model.resource.Resource;

/**
 * Grants access to Resources based on the ResourceModel
 * it support building URLs to existing resources, streaming existing resources and creating new resources   
 * @author dirk.scheffler
 *
 */
@SuppressWarnings("unusable-by-js")
public interface ResourceReadAccess {
	/**
	 * opens a stream to the binary data which is represented by the given resource
	 * @param resource the resource whose binary content is to be streamed
	 * @return the stream that will have the binary data
	 */
	InputStream openStream(Resource resource) throws IOException;
	
	/**
	 * writes the binary data which is represented by the given resource to the given output stream
	 * @param resource the resource whose binary content is to be streamed
	 * @param outputStream the stream which will receive the binary data
	 */
	void writeToStream(Resource resource, OutputStream outputStream) throws IOException;
	

}
