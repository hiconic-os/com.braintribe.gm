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
package dev.hiconic.servlet.webrpc.server.multipart;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.model.resource.Resource;

public class PartExpectationManager {

	private Set<String> expectedParts;
	private final Set<String> foundParts = new HashSet<>();

	public void expect(Iterable<Resource> resources) {
		expectedParts = new HashSet<>();

		for (Resource resource : resources) {
			if (resource.isTransient()) {
				expectedParts.add(resource.getGlobalId());
			}
		}

		expectedParts.removeAll(foundParts);
	}

	public boolean isLastExpected(String name) {
		foundParts.add(name);
		if (expectedParts != null) {
			expectedParts.remove(name);
			return expectedParts.isEmpty();
		} else {
			return false;
		}
	}

}
