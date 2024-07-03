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

import static com.braintribe.model.generic.value.EntityReferenceType.global;

import com.braintribe.cc.lcd.HashingComparator;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.utils.lcd.CommonTools;

/**
 * 
 */
public class EntRefHashingComparator implements HashingComparator<EntityReference> {

	/* Based on DEVCX-1032 we consider a null partition to be equal to any partition. For this reason we cannot have
	 * partition as part of the hash-code. */

	public static final EntRefHashingComparator INSTANCE = new EntRefHashingComparator();

	@Override
	public boolean compare(EntityReference e1, EntityReference e2) {
		if (e1.referenceType() != e2.referenceType())
			return false;

		/* Comparing IDs first, because this comparison is much faster in case the values are different (in case they
		 * are the same, signatures will probably also be the same) */

		// @formatter:off
		return 	e1.getRefId().equals(e2.getRefId()) &&
				e1.getTypeSignature().equals(e2.getTypeSignature()) &&
				// partition is ignored when using global references.
				// the only other option are persistent references, as preliminary would never have the same id  
				(e1.referenceType() == global || arePartitionsEqual(e1.getRefPartition(), e2.getRefPartition()));
		// @formatter:on
	}

	private boolean arePartitionsEqual(String p1, String p2) {
		return CommonTools.equalsOrBothNull(p1, p2) || EntityReference.ANY_PARTITION.equals(p1) || EntityReference.ANY_PARTITION.equals(p2);
	}

	@Override
	public int computeHash(EntityReference ref) {
		return PartitionIgnoringEntRefHashingComparator.INSTANCE.computeHash(ref);
	}
}
