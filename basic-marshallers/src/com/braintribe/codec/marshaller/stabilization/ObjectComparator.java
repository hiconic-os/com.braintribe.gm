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
package com.braintribe.codec.marshaller.stabilization;

import java.util.Comparator;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;

public class ObjectComparator implements Comparator<Object> {
	private static final GenericModelTypeReflection TYPE_REFLECTION = GMF.getTypeReflection();
	public static final ObjectComparator INSTANCE = new ObjectComparator();
	
	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == o2)
			return 0;
		
		if (o1 == null)
			return -1;
		
		if (o2 == null)
			return 1;
		
		GenericModelType t1 = TYPE_REFLECTION.getType(o1);
		GenericModelType t2 = TYPE_REFLECTION.getType(o2);
		
		int res = t1.compareTo(t2);
		
		if (res != 0)
			return res;
		
		@SuppressWarnings("unchecked")
		Comparator<Object> comparator = (Comparator<Object>) StabilizationComparators.comparator(t1);
		return comparator.compare(o1, o2);
	}
}
