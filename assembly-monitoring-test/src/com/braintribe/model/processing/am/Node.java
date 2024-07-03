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
package com.braintribe.model.processing.am;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@SelectiveInformation("Node ${name}")
@ToStringInformation("Node ${name}")
public interface Node extends StandardIdentifiable {

	EntityType<Node> T = EntityTypes.T(Node.class);

	void setName(String name);
	String getName();

	Node getLink1();
	void setLink1(Node successor);

	Node getLink2();
	void setLink2(Node predecessor);

	Set<Node> getLinkSet();
	void setLinkSet(Set<Node> set);

	Map<Node, Node> getLinkMap();
	void setLinkMap(Map<Node, Node> map);
}
