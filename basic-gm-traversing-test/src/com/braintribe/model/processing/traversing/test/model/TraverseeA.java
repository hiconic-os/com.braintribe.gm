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
package com.braintribe.model.processing.traversing.test.model;

import java.util.List;
import java.util.Map;
import java.util.Set;


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.processing.traversing.engine.impl.misc.model.EnumA;


public interface TraverseeA extends NamableEntity {

	EntityType<TraverseeA> T = EntityTypes.T(TraverseeA.class);
	
	// @formatter:off
	EnumA getEnumA();
	void setEnumA(EnumA enumA);

	TraverseeA getSomeA();
	void setSomeA(TraverseeA someA);

	TraverseeA getSomeOtherA();
	void setSomeOtherA(TraverseeA someOtherA);

	Set<TraverseeA> getSetA();
	void setSetA(Set<TraverseeA> setA);

	List<TraverseeA> getListA();
	void setListA(List<TraverseeA> listA);

	Map<Integer, TraverseeA> getMapIntA();
	void setMapIntA(Map<Integer, TraverseeA> mapIntA);

	Map<TraverseeA, TraverseeA> getMapAA();
	void setMapAA(Map<TraverseeA, TraverseeA> mapAA);
	// @formatter:on

}
