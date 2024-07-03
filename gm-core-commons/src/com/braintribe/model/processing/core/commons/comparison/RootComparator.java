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

import com.braintribe.model.generic.GMF;
import com.braintribe.model.processing.traversing.api.path.TraversingRootModelPathElement;

public class RootComparator extends ObjectComparator {

	public RootComparator(AssemblyComparison assemblyComparison) {
		super(assemblyComparison, false);
	}
	
	@Override
	public int compare(Object o1, Object o2) {
		
		assemblyComparison.pushElement(p -> new TraversingRootModelPathElement(p, o1, GMF.getTypeReflection().getType(o1)));
		
		int res = super.compare(o1, o2);
		
		if (res != 0)
			return res;
		else
			assemblyComparison.popElement();
		
		return 0;
	}
}
