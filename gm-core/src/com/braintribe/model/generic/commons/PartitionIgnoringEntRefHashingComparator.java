// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.generic.commons;

import com.braintribe.cc.lcd.HashingComparator;
import com.braintribe.model.generic.value.EntityReference;

/**
 * 
 */
public class PartitionIgnoringEntRefHashingComparator implements HashingComparator<EntityReference> {

	public static final PartitionIgnoringEntRefHashingComparator INSTANCE = new PartitionIgnoringEntRefHashingComparator();

	@Override
	public boolean compare(EntityReference e1, EntityReference e2) {
		if (e1.referenceType() != e2.referenceType()) {
			return false;
		}

		/* Comparing IDs first, because this comparison is much faster in case the values are different (in case they
		 * are the same, signatures will probably also be the same) */

		// @formatter:off
		return 	e1.getRefId().equals(e2.getRefId()) &&
				e1.getTypeSignature().equals(e2.getTypeSignature());
		// @formatter:on
	}

	@Override
	public int computeHash(EntityReference ref) {
		return 31 * ref.getTypeSignature().hashCode() + ref.getRefId().hashCode();
	}
}
