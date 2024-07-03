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
package com.braintribe.model.processing.manipulator.expert.basic.collection;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.processing.manipulator.api.CollectionManipulator;

public class SetManipulator implements CollectionManipulator<Set<Object>, Object> {

	public static final SetManipulator INSTANCE = new SetManipulator();

	private SetManipulator() {
	}
	
	@Override
	public void insert(Set<Object> set, Object index, Object value) {
		set.add(value);
	}

	@Override
	public void insert(Set<Object> set, Map<Object, Object> values) {
		set.addAll(values.values());
	}

	@Override
	public void remove(Set<Object> set, Object index, Object value) {
		set.remove(index);
	}

	@Override
	public void remove(Set<Object> set, Map<Object, Object> values) {
		set.removeAll(values.keySet());
	}

	@Override
	public void clear(Set<Object> set) {
		set.clear();
	}

}
