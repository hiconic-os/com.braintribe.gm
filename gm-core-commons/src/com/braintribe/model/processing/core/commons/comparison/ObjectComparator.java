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
package com.braintribe.model.processing.core.commons.comparison;

import java.util.Comparator;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;

public class ObjectComparator implements Comparator<Object> {
	
	public static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();
	
	protected AssemblyComparison assemblyComparison;
	private boolean internal;
	
	public ObjectComparator(AssemblyComparison assemblyComparison, boolean internal) {
		super();
		this.assemblyComparison = assemblyComparison;
		this.internal = internal;
	}

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == o2)
			return 0;
		
		if (o1 == null)
			return -1;
		
		if (o2 == null)
			return 1;
		
		GenericModelType t1 = typeReflection.getType(o1);
		GenericModelType t2 = typeReflection.getType(o2);
		
		int res = t1.getTypeSignature().compareTo(t2.getTypeSignature());
		
		if (res != 0) {
			if (!internal)
				assemblyComparison.setMismatchDescription("type mismatch: " + t1.getTypeSignature() + " vs. " + t2.getTypeSignature());
			return res;
		}
		
		Comparator<Object> comparator = internal? 
				assemblyComparison.getInternalComparator(t1):
				assemblyComparison.getComparator(t1);
		
		return comparator.compare(o1, o2);
	}
}
