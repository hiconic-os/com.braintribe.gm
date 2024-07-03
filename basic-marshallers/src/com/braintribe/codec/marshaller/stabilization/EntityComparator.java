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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

public class EntityComparator implements Comparator<GenericEntity> {
	
	public static final EntityComparator INSTANCE = new EntityComparator();
	
	@Override
	public int compare(GenericEntity o1, GenericEntity o2) {
		if (o1 == o2)
			return 0;
		
		if (o1 == null)
			return -1;
		
		if (o2 == null)
			return 1;
		
		EntityType<?> t1 = o1.entityType();
		EntityType<?> t2 = o2.entityType();
		
		int res = t1.getShortName().compareTo(t2.getShortName());
		
		if (res != 0)
			return res;
		
		res = t1.compareTo(t2);
		
		if (res != 0)
			return res;
		
		NormalizedId id1 = new NormalizedId(o1);
		NormalizedId id2 = new NormalizedId(o2);
		
		return id1.compareTo(id2);
	}
}
