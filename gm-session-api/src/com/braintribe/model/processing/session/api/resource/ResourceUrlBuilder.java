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

/**
 * URL builder for upload and download of a {@link Resource}.
 */
public interface ResourceUrlBuilder {

	ResourceUrlBuilder download(boolean download);

	ResourceUrlBuilder fileName(String fileName);

	ResourceUrlBuilder accessId(String accessId);

	ResourceUrlBuilder sessionId(String sessionId);

	ResourceUrlBuilder sourceType(String sourceTypeSignature);

	ResourceUrlBuilder useCase(String useCase);

	ResourceUrlBuilder mimeType(String mimeType);

	ResourceUrlBuilder md5(String md5);

	ResourceUrlBuilder creator(String creator);

	ResourceUrlBuilder tags(String tags);

	ResourceUrlBuilder specification(String specification);

	/** Determines the base URL. */
	ResourceUrlBuilder base(String baseUrl);

	String asString();

	/**
	 * @deprecated Use {@link #download(boolean)} instead. This method will be removed in a future release.
	 */
	@Deprecated
	default ResourceUrlBuilder forDownload(boolean download) {
		return download(download);
	}

	/**
	 * @deprecated Use {@link #fileName(String)} instead. This method will be removed in a future release.
	 */
	@Deprecated
	default ResourceUrlBuilder withDownloadName(String downloadName) {
		return fileName(downloadName);
	}

	/**
	 * @deprecated Use {@link #fileName(String)} instead. This method will be removed in a future release.
	 */
	@Deprecated
	default ResourceUrlBuilder withFileName(String fileName) {
		return fileName(fileName);
	}

	/**
	 * @deprecated Use {@link #accessId(String)} instead. This method will be removed in a future release.
	 */
	@Deprecated
	default ResourceUrlBuilder forAccess(String accessId) {
		return accessId(accessId);
	}

	/**
	 * @deprecated Use {@link #sessionId(String)} instead. This method will be removed in a future release.
	 */
	@Deprecated
	default ResourceUrlBuilder withSessionId(String sessionId) {
		return sessionId(sessionId);
	}

	/**
	 * @deprecated Use {@link #sourceType(String)} instead. This method will be removed in a future release.
	 */
	@Deprecated
	default ResourceUrlBuilder withSourceType(String sourceTypeSignature) {
		return sourceType(sourceTypeSignature);
	}

}
