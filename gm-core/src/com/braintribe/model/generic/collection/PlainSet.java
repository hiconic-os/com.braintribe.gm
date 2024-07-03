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
package com.braintribe.model.generic.collection;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.braintribe.model.generic.reflection.SetType;

/**
 * @author peter.gazdik
 */
public class PlainSet<E> extends LinkedHashSet<E> implements SetBase<E> {

	private static final long serialVersionUID = -6029303086432557408L;

	private final SetType setType;

	public PlainSet(SetType setType) {
		this.setType = setType;
	}

	public PlainSet(SetType setType, Collection<? extends E> c) {
		super(c);
		this.setType = setType;
	}

	@Override
	public SetType type() {
		return setType;
	}

}
