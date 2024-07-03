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
package com.braintribe.codec.marshaller.dom.coder.collection;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.codec.marshaller.dom.coder.DomCoder;

public class SetDomCoder<T> extends CollectionDomCoder<T, Set<T>> {
	
	public SetDomCoder(DomCoder<T> elementCoder) {
		super(elementCoder, "S");
	}
	public SetDomCoder(DomCoder<T> elementCoder, boolean returnNullOnEmptyCollection) {
		super(elementCoder, "S", returnNullOnEmptyCollection);
	}
	
	@Override
	protected Set<T> createCollection() {
		return new HashSet<T>();
	}
}
