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
package com.braintribe.model.service.api;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * InstanceId addresses a certain application on a node in a clustered runtime environment. It can be also used in a
 * wildcarded way using * for either applicationId or nodeId
 *
 */
@ToStringInformation("${applicationId}@${nodeId}")
public interface InstanceId extends GenericEntity {

	EntityType<InstanceId> T = EntityTypes.T(InstanceId.class);

	String applicationId = "applicationId";
	String nodeId = "nodeId";

	String getApplicationId();
	void setApplicationId(String applicationId);

	String getNodeId();
	void setNodeId(String nodeId);

	@Override
	default String stringify() {
		String appId = getApplicationId();
		String nodeId = getNodeId();
		if (appId == null) {
			return nodeId != null ? "<undefined>@".concat(nodeId) : "<undefined>";
		} else if (nodeId == null) {
			return appId.concat("@<undefined>");
		}
		return appId.concat("@").concat(nodeId);
	}

	static InstanceId parse(String stringified) {
		if (stringified == null) {
			return null;
		}
		int index = stringified.indexOf('@');
		if (index <= 0) {
			throw new IllegalArgumentException("Could not find a @ in " + stringified);
		}
		String appId = stringified.substring(0, index);
		if (appId.equals("<undefined>")) {
			appId = null;
		}
		String nodeId = stringified.substring(index + 1);
		if (nodeId.equals("<undefined>")) {
			nodeId = null;
		}
		return of(nodeId, appId);
	}

	static InstanceId of(String nodeId, String appId) {
		InstanceId bean = InstanceId.T.create();
		bean.setNodeId(nodeId);
		bean.setApplicationId(appId);
		return bean;
	}
}
