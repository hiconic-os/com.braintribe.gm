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
package com.braintribe.model.processing.service.common.topology;

import java.util.Comparator;

import com.braintribe.model.service.api.InstanceId;

public class InstanceIdComparator implements Comparator<InstanceId> {

	public static InstanceIdComparator instance = new InstanceIdComparator();
	
	private InstanceIdComparator() {
		//Do nothing; use instance instead
	}
	
	@Override
	public int compare(InstanceId o1, InstanceId o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		if (o1 == null) {
			return 1;
		}
		if (o2 == null) {
			return -1;
		}
		
		String leftNode = o1.getNodeId();
		String rightNode = o2.getNodeId();
		
		if (leftNode == null && rightNode != null) {
			return 1;
		}
		if (rightNode == null && leftNode != null) {
			return -1;
		}

		if (leftNode != null && rightNode != null) {
			int cmp = leftNode.compareTo(rightNode);
			if (cmp != 0) {
				return cmp;
			}
		}
		
		String leftApp = o1.getApplicationId();
		String rightApp = o2.getApplicationId();
		
		if (leftApp == null || rightApp == null) {
			if (leftApp == null) {
				return 1;
			}
			if (rightApp == null) {
				return -1;
			}
		}
		
		return leftApp.compareTo(rightApp);
	}

}
