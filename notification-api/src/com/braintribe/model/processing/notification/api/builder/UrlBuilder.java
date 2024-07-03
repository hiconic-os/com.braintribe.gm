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
package com.braintribe.model.processing.notification.api.builder;

import com.braintribe.model.uicommand.GotoUrl;

/**
 * Builder API to fluently create a {@link GotoUrl} command. 
 */
public interface UrlBuilder {

	/**
	 * Sets the URL. 
	 */
	UrlBuilder url(String url);
	/**
	 * Sets the target of the URL
	 */
	UrlBuilder target(String target);
	/**
	 * Indicates that an image should be used when displaying the link.
	 */
	UrlBuilder useImage();
	
	/**
	 * Builds the {@link GotoUrl} command based on previous settings.
	 */
	NotificationBuilder close();
}
