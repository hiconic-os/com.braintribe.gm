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
package com.braintribe.model.generic.commons;

import java.util.Comparator;

import com.braintribe.model.generic.value.EntityReference;

/**
 * @deprecated it seems it makes little sense to compare references while ignoring partitions
 */
@Deprecated
public class PartitionIgnoringEntityReferenceComparator implements Comparator<EntityReference> {

	public final static PartitionIgnoringEntityReferenceComparator INSTANCE = new PartitionIgnoringEntityReferenceComparator();

	@Override
	public int compare(EntityReference o1, EntityReference o2) {
		if (o1 == o2)
			return 0;

		if (o1.referenceType() != o2.referenceType()) {
			return o1.referenceType().compareTo(o2.referenceType());
		}

		String t1 = o1.getTypeSignature();
		String t2 = o2.getTypeSignature();

		if (t1 != t2) {
			if (t1 == null)
				return -1;

			if (t2 == null)
				return 1;

			int res = t1.compareTo(t2);
			if (res != 0)
				return res;
		}

		Comparable<Object> id1 = (Comparable<Object>) o1.getRefId();
		Comparable<Object> id2 = (Comparable<Object>) o2.getRefId();

		if (id1 == id2)
			return 0;
		if (id1 == null)
			return -1;
		if (id2 == null)
			return 1;

		return id1.compareTo(id2);
	}

}
