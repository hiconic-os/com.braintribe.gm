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
package com.braintribe.model.processing.smood.test;

import java.util.List;

import com.braintribe.cc.lcd.HashingComparator;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.record.ListRecord;

/**
 * 
 */
public class RecordHashingComparator implements HashingComparator<ListRecord> {

	public static final RecordHashingComparator INSTANCE = new RecordHashingComparator(true);

	private final boolean strictEntityComparison;

	/**
	 * @param strictEntityComparison
	 *            if set to <tt>true</tt>, entities are compared using <tt>equals</tt> method, i.e. it is an identity
	 *            check. If the flag is set to <tt>false</tt>, a entity-reference-like check is performed (i.e.
	 *            signature and id are compared)
	 */
	public RecordHashingComparator(boolean strictEntityComparison) {
		this.strictEntityComparison = strictEntityComparison;
	}

	@Override
	public boolean compare(ListRecord t1, ListRecord t2) {
		List<Object> v1 = t1.getValues();
		List<Object> v2 = t2.getValues();

		int size = v1.size();

		if (v2.size() != size)
			return false;

		for (int i = 0; i < size; i++) {
			Object o1 = v1.get(i);
			Object o2 = v2.get(i);

			if (!compareValues(o1, o2))
				return false;
		}

		return true;
	}

	@Override
	public int computeHash(ListRecord t) {
		int result = 0;

		for (Object o : t.getValues())
			result = 31 * result + hashValue(o);

		return result;
	}

	private boolean compareValues(Object o1, Object o2) {
		if (o1 == o2)
			return true;

		if (o1 == null)
			return false;

		boolean areEqual = o1.equals(o2);
		if (strictEntityComparison || areEqual)
			return areEqual;

		if (!isGenericEntity(o1) || !(isGenericEntity(o2)))
			return false;

		GenericEntity ge1 = (GenericEntity) o1;
		GenericEntity ge2 = (GenericEntity) o2;

		if (ge1.entityType() != ge2.entityType())
			return false;

		Object id1 = ge1.getId();
		Object id2 = ge2.getId();

		if (id1 == null || id2 == null)
			throw new RuntimeException("Cannot compare instances. At least one has no id. Instances: " + o1 + ", " + o2);

		return id1.equals(id2);
	}

	private static boolean isGenericEntity(Object o) {
		return o instanceof GenericEntity;
	}

	private int hashValue(Object o) {
		return o == null ? 1 : nonNullHash(o);
	}

	private int nonNullHash(Object o) {
		if (strictEntityComparison || !(isGenericEntity(o)))
			return o.hashCode();

		GenericEntity ge = (GenericEntity) o;

		return 31 * ge.entityType().getTypeSignature().hashCode() + hashValue(ge.getId());
	}
}
