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
package com.braintribe.model.processing.service.impl.topology;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.processing.service.common.topology.InstanceIdHashingComparator;
import com.braintribe.model.service.api.InstanceId;

public class InstanceIdHashingComparatorTest {

	@Test
	public void testSimple() throws Exception {

		InstanceIdHashingComparator comp = InstanceIdHashingComparator.instance;
		
		
		assertThat(comp.compare(createInstanceId(null, null), createInstanceId(null, null))).isTrue();
		assertThat(comp.compare(createInstanceId("n1", null), createInstanceId("n1", null))).isTrue();
		assertThat(comp.compare(createInstanceId(null, "a1"), createInstanceId(null, "a1"))).isTrue();
		
		assertThat(comp.compare(createInstanceId("n1", null), createInstanceId("n2", null))).isFalse();
		assertThat(comp.compare(createInstanceId(null, "a1"), createInstanceId(null, "a2"))).isFalse();

		assertThat(comp.compare(createInstanceId("n1", null), createInstanceId("n2", null))).isFalse();
		assertThat(comp.compare(createInstanceId(null, "a1"), createInstanceId(null, "a2"))).isFalse();

	}

	private static InstanceId createInstanceId(String nodeId, String appId) {
		InstanceId iid = InstanceId.T.create();
		iid.setNodeId(nodeId);
		iid.setApplicationId(appId);
		return iid;
	}
}
