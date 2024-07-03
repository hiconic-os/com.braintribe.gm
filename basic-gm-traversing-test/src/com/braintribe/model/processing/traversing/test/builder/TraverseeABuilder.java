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
package com.braintribe.model.processing.traversing.test.builder;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.processing.traversing.test.model.TraverseeA;

/**
 * 
 */
public class TraverseeABuilder extends AbstractBuilder<TraverseeA, TraverseeABuilder> {

	public TraverseeABuilder() {
		super(TraverseeA.class);
	}

	public TraverseeABuilder name(String value) {
		instance.setName(value);
		return self;
	}

	public TraverseeABuilder someA(TraverseeA value) {
		instance.setSomeA(value);
		return self;
	}

	public TraverseeABuilder someOtherA(TraverseeA value) {
		instance.setSomeOtherA(value);
		return self;
	}

	public TraverseeABuilder addToSetA(TraverseeA... values) {
		Set<TraverseeA> as = instance.getSetA();
		if (as == null) {
			as = new HashSet<TraverseeA>();
			instance.setSetA(as);
		}

		as.addAll(asList(values));
		return self;
	}

	public TraverseeABuilder addToListA(TraverseeA... values) {
		List<TraverseeA> as = instance.getListA();
		if (as == null) {
			as = new ArrayList<TraverseeA>();
			instance.setListA(as);
		}

		as.addAll(asList(values));
		return self;
	}

	public TraverseeABuilder addToIntA(Integer key, TraverseeA value) {
		Map<Integer, TraverseeA> as = instance.getMapIntA();
		if (as == null) {
			as = new HashMap<Integer, TraverseeA>();
			instance.setMapIntA(as);
		}

		as.put(key, value);
		return self;
	}

	public TraverseeABuilder addToAA(TraverseeA key, TraverseeA value) {
		Map<TraverseeA, TraverseeA> as = instance.getMapAA();
		if (as == null) {
			as = new HashMap<TraverseeA, TraverseeA>();
			instance.setMapAA(as);
		}

		as.put(key, value);
		return self;
	}
}
