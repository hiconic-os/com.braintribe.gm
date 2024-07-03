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

import com.braintribe.cc.lcd.HashingComparator;
import com.braintribe.model.service.api.InstanceId;

public class InstanceIdHashingComparator implements HashingComparator<InstanceId> {

	public final static InstanceIdHashingComparator instance = new InstanceIdHashingComparator();
	
	@Override
	public boolean compare(InstanceId e1, InstanceId e2) {
		if (e1 == null && e2 == null) {
			return true;
		}
		if (e1 == null) {
			return false;
		}
		if (e2 == null) {
			return false;
		}
		
		String leftNode = e1.getNodeId();
		String rightNode = e2.getNodeId();
		
		if (leftNode == null && rightNode != null) {
			return false;
		}
		if (rightNode == null && leftNode != null) {
			return false;
		}

		if (leftNode != null && rightNode != null) {
			if (!leftNode.equals(rightNode)) {
				return false;
			}
		}
		
		String leftApp = e1.getApplicationId();
		String rightApp = e2.getApplicationId();
		
		if (leftApp == null && rightApp != null) {
			return false;
		}
		if (rightApp == null && leftApp != null) {
			return false;
		}

		if (leftApp != null && rightApp != null) {
			if (!leftApp.equals(rightApp)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int computeHash(InstanceId e) {
		
		if (e == null) {
			return 0;
		}
		String text = e.stringify();
		return text.hashCode();
	}

}
