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

import java.util.LinkedHashMap;
import java.util.Map;

import com.braintribe.model.generic.reflection.MapType;

/**
 * @author peter.gazdik
 */
public class PlainMap<K, V> extends LinkedHashMap<K, V> implements MapBase<K, V> {

	private static final long serialVersionUID = -6029303086432557408L;

	private final MapType mapType;

	public PlainMap(MapType mapType) {
		this.mapType = mapType;
	}

	public PlainMap(MapType mapType, Map<? extends K, ? extends V> m) {
		super(m);
		this.mapType = mapType;
	}

	@Override
	public MapType type() {
		return mapType;
	}

}
