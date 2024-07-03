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
package com.braintribe.model.processing.query.eval.tools;

import java.util.Comparator;

import com.braintribe.model.generic.GenericEntity;

/**
 * 
 */
public class EntityComparator implements Comparator<GenericEntity> {

	public static final EntityComparator INSTANCE = new EntityComparator();

	private EntityComparator() {
	}

	@Override
	public int compare(GenericEntity e1, GenericEntity e2) {
		long result = e1.runtimeId() - e2.runtimeId();

		return result > 0 ? 1 : (result < 0 ? -1 : 0);
	}

}
