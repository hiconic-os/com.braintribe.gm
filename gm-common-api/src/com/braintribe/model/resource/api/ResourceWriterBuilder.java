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

import java.util.Date;

import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.specification.ResourceSpecification;
import com.braintribe.utils.io.WriterBuilder;

/**
 * {@link WriterBuilder} for a {@link Resource}.
 * 
 * @author peter.gazdik
 */
public interface ResourceWriterBuilder extends WriterBuilder<Resource> {

	@Override
	/** Value to be used for {@link Resource#getName()} of the resulting resource. */
	ResourceWriterBuilder withName(String name);

	/** Value to be used for {@link Resource#getMimeType() mimeType} of the resulting resource. */
	ResourceWriterBuilder withMimeType(String mimeType);

	/** Value to be used for {@link Resource#getTags() tags} of the resulting resource. */
	ResourceWriterBuilder withTags(String... tags);

	/** Value to be used for {@link Resource#getMd5() md5} of the resulting resource. */
	ResourceWriterBuilder withMd5(String md5);

	/** Value to be used for {@link Resource#getFileSize() fileSize} of the resulting resource. */
	ResourceWriterBuilder withFileSize(Long fileSize);

	/** Value to be used for {@link Resource#getCreated() created} of the resulting resource. */
	ResourceWriterBuilder withCreated(Date date);

	/** Value to be used for {@link Resource#getCreator() creator} of the resulting resource. */
	ResourceWriterBuilder withCreator(String creator);

	/** Value to be used for {@link Resource#getSpecification() specification} of the resulting resource. */
	ResourceWriterBuilder withSpecification(ResourceSpecification specification);

	/** Writes the Resource data as a copy of given resource. */
	default Resource fromResource(Resource resource) {
		return fromInputStreamFactory(resource::openStream);
	}

}
