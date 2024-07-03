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

public class ComparableComparator implements Comparator<Object> {
	private boolean internal;
	private AssemblyComparison assemblyComparision;
	
	public ComparableComparator(boolean internal, AssemblyComparison assemblyComparision) {
		super();
		this.internal = internal;
		this.assemblyComparision = assemblyComparision;
	}

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == o2)
			return 0;
		
		int res = 0;
		
		if (o1 == null)  
			res = -1;
		else if (o2 == null)
			res = 1;
		else { 
			Comparable<Object> c1 = (Comparable<Object>)o1;
			res = c1.compareTo(o2);
		}
		
		if (res != 0) {
			if (!internal)
				assemblyComparision.setMismatchDescription("value mismatch: " + o1 + " vs. " + o2);
		}
		
		return res;
	}
}
