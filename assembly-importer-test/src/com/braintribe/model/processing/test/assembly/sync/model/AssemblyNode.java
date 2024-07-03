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
package com.braintribe.model.processing.test.assembly.sync.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
public interface AssemblyNode extends GenericEntity {

	EntityType<AssemblyNode> T = EntityTypes.T(AssemblyNode.class);

	String neighbors= "neighbors";
	
	String getName();
	void setName(String value);

	List<AssemblyNode> getNeighbors();
	void setNeighbors(List<AssemblyNode> value);

	Object getObject();
	void setObject(Object value);
	
	List<Integer> getIntList();
	void setIntList(List<Integer> value);
	
	Set<Integer> getIntSet();
	void setIntSet(Set<Integer> value);
	
	Map<Integer, Integer> getIntMap();
	void setIntMap(Map<Integer, Integer> value);

}
