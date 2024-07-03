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
package com.braintribe.model.processing.manipulation.basic.normalization;

import com.braintribe.cc.lcd.HashingComparator;
import com.braintribe.model.generic.commons.EntRefHashingComparator;
import com.braintribe.model.generic.value.EntityReference;

/**
 * 
 */
class ElementHashingComparator implements HashingComparator<Object> {

	public static final ElementHashingComparator INSTANCE = new ElementHashingComparator();

	private static final EntRefHashingComparator referenceComparator = EntRefHashingComparator.INSTANCE;

	private ElementHashingComparator() {
	}

	@Override
	public int computeHash(Object e) {
		if (e == null)
			return 0;

		if (e instanceof EntityReference) {
			return referenceComparator.computeHash((EntityReference) e);

		} else {
			return e.hashCode();
		}
	}

	@Override
	public boolean compare(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}

		if (o2 == null) {
			return false;
		}

		if (o1 instanceof EntityReference) {
			if (o2 instanceof EntityReference) {
				return referenceComparator.compare((EntityReference) o1, (EntityReference) o2);
			} else {
				return false;
			}
		}

		return o1.equals(o2);
	}

}
