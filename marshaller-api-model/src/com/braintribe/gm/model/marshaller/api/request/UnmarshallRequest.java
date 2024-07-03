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
package com.braintribe.gm.model.marshaller.api.request;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;

/**
 * UnmarshallRequest is the base type for all request types that are intended to unmarshall (deserialize) a {@link Resource} into data based on a marshaller that is 
 * either selected by the {@link Resource#getMimeType()} property if given or a mimetype detection on the actual contents of the {@link Resource}.
 * @author Dirk Scheffler
 */
@Abstract
public interface UnmarshallRequest extends AbstractMarshallRequest {
	EntityType<UnmarshallRequest> T = EntityTypes.T(UnmarshallRequest.class);

	/**
	 * The {@link Resource} that will be should be unmarshalled. If given, its {@link Resource#getMimeType()} will be used to select a marshaller. Otherwise the mimetype will be detected from
	 * the contents of the {@link Resource}.  
	 */
	@Mandatory
	Resource getResource();
	void setResource(Resource resource);
}
