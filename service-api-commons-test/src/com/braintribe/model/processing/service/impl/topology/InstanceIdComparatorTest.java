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

import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

import org.junit.Test;

import com.braintribe.model.processing.service.common.topology.InstanceIdComparator;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.utils.StringTools;

public class InstanceIdComparatorTest {

	@Test
	public void testComparatorNodeIdSimple() throws Exception {

		TreeSet<InstanceId> set = new TreeSet<>(InstanceIdComparator.instance);

		set.add(createInstanceId("3", "1"));
		set.add(createInstanceId("2", "1"));
		set.add(createInstanceId("1", "1"));

		Iterator<InstanceId> iterator = set.iterator();
		for (int i=1; i<3; ++i) {
			assertThat(iterator.next().getNodeId()).isEqualTo(""+i);
		}
	}

	@Test
	public void testComparatorNodeIdRandom() throws Exception {

		TreeSet<InstanceId> set = new TreeSet<>(InstanceIdComparator.instance);

		int count = 1000;
		Random rnd = new Random();
		for (int i=0; i<count; ++i) {
			set.add(createInstanceId(StringTools.extendStringInFront(""+rnd.nextInt(100000), '0', 7), "1"));

		}

		int latest = -1;
		for (InstanceId iid : set) {
			int current = Integer.parseInt(iid.getNodeId());
			assertThat(current).isGreaterThanOrEqualTo(latest);
			latest = current;
		}
	}

	@Test
	public void testComparatorMixedSimple() throws Exception {

		TreeSet<InstanceId> set = new TreeSet<>(InstanceIdComparator.instance);

		int count = 1000;
		Random rnd = new Random();
		for (int i=0; i<count; ++i) {
			set.add(createInstanceId(StringTools.extendStringInFront(""+rnd.nextInt(100000), '0', 7), StringTools.extendStringInFront(""+rnd.nextInt(100000), '0', 7)));
		}

		int latestNodeId = -1;
		int latestAppId = -1;
		for (InstanceId iid : set) {
			int currentNodeId = Integer.parseInt(iid.getNodeId());
			int currentAppId = Integer.parseInt(iid.getApplicationId());
			
			assertThat(currentNodeId).isGreaterThanOrEqualTo(latestNodeId);
			if (currentNodeId > latestNodeId) {
				latestAppId = -1;
			}
			assertThat(currentAppId).isGreaterThanOrEqualTo(latestAppId);
			
			latestNodeId = currentNodeId;
			latestAppId = currentAppId;
		}
	}

	@Test
	public void testComparatorNoNodeIdSimple() throws Exception {

		TreeSet<InstanceId> set = new TreeSet<>(InstanceIdComparator.instance);

		set.add(createInstanceId(null, "1"));
		set.add(createInstanceId(null, "3"));
		set.add(createInstanceId(null, "2"));

		Iterator<InstanceId> iterator = set.iterator();
		for (int i=1; i<3; ++i) {
			assertThat(iterator.next().getApplicationId()).isEqualTo(""+i);
		}
	}
	
	@Test
	public void testComparatorMixedNodeIdSimple() throws Exception {

		TreeSet<InstanceId> set = new TreeSet<>(InstanceIdComparator.instance);

		set.add(createInstanceId(null, "3"));
		set.add(createInstanceId("1", "1"));
		set.add(createInstanceId(null, "2"));

		Iterator<InstanceId> iterator = set.iterator();
		for (int i=1; i<3; ++i) {
			assertThat(iterator.next().getApplicationId()).isEqualTo(""+i);
		}
	}
	
	private static InstanceId createInstanceId(String nodeId, String appId) {
		InstanceId iid = InstanceId.T.create();
		iid.setNodeId(nodeId);
		iid.setApplicationId(appId);
		return iid;
	}
}
