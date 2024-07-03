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
package com.braintribe.model.generic.template;

import com.braintribe.model.generic.GmCoreApiInteropNamespaces;

import jsinterop.annotations.JsType;

/**
 * <h3>Example</h3> "Hello ${name}"
 * <p>
 * consists of two fragments:
 * <ol>
 * <li>"Hello "
 * <li>"name"
 * </ol>
 * 
 * First is plain text, second is a {@link #isPlaceholder() placeholder}.
 */
@JsType(namespace = GmCoreApiInteropNamespaces.template)
public interface TemplateFragment {

	/**
	 * Placeholder fragment is created from the content of a placeholder expression, e.g. "name" in "Hello ${name}".
	 */
	boolean isPlaceholder();

	/**
	 * The text of either a static template part or the text content of a placeholder expression such as ${name}
	 */
	String getText();
}
