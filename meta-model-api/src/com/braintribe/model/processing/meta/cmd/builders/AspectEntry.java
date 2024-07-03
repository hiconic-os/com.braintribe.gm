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
package com.braintribe.model.processing.meta.cmd.builders;

import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;

/**
 * 
 */
public class AspectEntry<T, A extends SelectorContextAspect<? super T>> {

	private Class<A> aspect;
	private T value;

	public AspectEntry() {
	}

	public AspectEntry(Class<A> aspect, T value) {
		this.aspect = aspect;
		this.value = value;
	}

	public Class<A> getAspect() {
		return aspect;
	}

	public void setAspect(Class<A> aspect) {
		this.aspect = aspect;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

}
