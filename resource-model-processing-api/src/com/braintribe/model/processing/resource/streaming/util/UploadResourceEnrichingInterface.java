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
package com.braintribe.model.processing.resource.streaming.util;

import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.specification.ResourceSpecification;

public interface UploadResourceEnrichingInterface {

	void toResource(Resource resource);

	InputStream wrapInputStream(InputStream rawInput);

	Long getLength();

	void setLength(Long length);

	String getName();

	void setName(String name);

	Date getCreated();

	void setCreated(Date created);

	String getMd5();

	void setMd5(String md5);

	String getMimeType();

	void setMimeType(String mimeType);

	String getCreator();

	void setCreator(String creator);

	Set<String> getTags();

	void setTags(Set<String> tags);

	ResourceSpecification getResourceSpecification();

	void setResourceSpecification(ResourceSpecification resourceSpecification);

}