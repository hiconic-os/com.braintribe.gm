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

import com.braintribe.model.processing.manipulator.api.CollectionManipulator;

public class MapManipulator implements CollectionManipulator<Map<Object, Object>, Object> {

	public static final MapManipulator INSTANCE = new MapManipulator();

	private MapManipulator() {
	}

	@Override
	public void insert(Map<Object, Object> map, Object index, Object value) {
		map.put(index, value);
	}

	@Override
	public void insert(Map<Object, Object> map, Map<Object, Object> values) {
		map.putAll(values);
	}

	@Override
	public void remove(Map<Object, Object> map, Object index, Object value) {
		map.remove(index);
	}

	@Override
	public void remove(Map<Object, Object> map, Map<Object, Object> values) {
		for (Object key: values.keySet()) {
			map.remove(key);
		}
	}

	@Override
	public void clear(Map<Object, Object> map) {
		map.clear();
	}

}
