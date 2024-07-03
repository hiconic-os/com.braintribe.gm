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

import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.ResourceSource;
import com.braintribe.model.resource.specification.ResourceSpecification;

/**
 * <p>
 * Builder for updating the binary data associated with {@link Resource} as well as the resource itself if needed.
 * 
 * @author Neidhart.Orlich
 */
public interface ResourceUpdateBuilder extends ResourceCreateBuilder {

	ResourceUpdateBuilder deleteOldResourceSource(boolean keep);

	@Override
	ResourceUpdateBuilder mimeType(String mimeType);
	@Override
	ResourceUpdateBuilder md5(String md5);
	@Override
	ResourceUpdateBuilder useCase(String useCase);
	@Override
	ResourceUpdateBuilder tags(Set<String> tags);
	@Override
	ResourceUpdateBuilder sourceType(EntityType<? extends ResourceSource> sourceType);
	@Override
	ResourceUpdateBuilder specification(ResourceSpecification specification);
	@Override
	ResourceUpdateBuilder name(String resourceName);
	@Override
	ResourceUpdateBuilder creator(String creator);

}
