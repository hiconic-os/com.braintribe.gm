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

import java.util.ArrayList;
import java.util.Collection;

import com.braintribe.model.generic.reflection.ListType;

/**
 * @author peter.gazdik
 */
public class PlainList<E> extends ArrayList<E> implements ListBase<E> {

	private static final long serialVersionUID = -6029303086432557408L;

	private final ListType listType;

	public PlainList(ListType listType) {
		this.listType = listType;
	}

	public PlainList(ListType listType, Collection<? extends E> value) {
		super(value);
		this.listType = listType;
	}

	@Override
	public ListType type() {
		return listType;
	}

}
