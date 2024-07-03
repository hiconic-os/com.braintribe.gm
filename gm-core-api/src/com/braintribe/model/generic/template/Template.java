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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.braintribe.model.generic.GmCoreApiInteropNamespaces;

import jsinterop.annotations.JsType;

@JsType(namespace = GmCoreApiInteropNamespaces.template)
public interface Template {
	List<TemplateFragment> fragments();
	
	String expression();
	
	default boolean isStaticOnly() {
		int size = fragments().size();
		return size == 0 || (size == 1 && !fragments().get(0).isPlaceholder());
	}

	default String evaluate(Function<String, String> placeholderResolver) {
		return fragments().stream() //
				.map(f -> f.isPlaceholder() ? placeholderResolver.apply(f.getText()): f.getText()) //
				.collect(Collectors.joining());
	}
	
	/**
	 * parses an expression that can contain a sequence of static text and placeholders like "static text with a ${placeholder}."
	 * @throws IllegalArgumentException if the expression has syntax errors.
	 */
	
	static Template parse(String expression) throws IllegalArgumentException {
		return TemplateParser.parse(expression);
	}
}
