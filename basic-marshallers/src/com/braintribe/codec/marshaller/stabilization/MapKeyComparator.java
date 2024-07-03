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
package com.braintribe.codec.marshaller.stabilization;

import java.util.Comparator;
import java.util.Map;

public class MapKeyComparator<T> implements Comparator<Map.Entry<T, ?>> {

	private Comparator<T> keyComparator;
	
	public MapKeyComparator(Comparator<T> keyComparator) {
		super();
		this.keyComparator = keyComparator;
	}

	@Override
	public int compare(Map.Entry<T, ?> o1, Map.Entry<T, ?> o2) {
		T k1 = o1.getKey();
		T k2 = o2.getKey();
		return keyComparator.compare(k1, k2);
	}
}
