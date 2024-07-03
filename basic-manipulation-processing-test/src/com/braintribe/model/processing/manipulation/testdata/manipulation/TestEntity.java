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
package com.braintribe.model.processing.manipulation.testdata.manipulation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.descriptive.HasName;
import com.braintribe.model.generic.StandardIntegerIdentifiable;

import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@ToStringInformation(value = "${name}")
public interface TestEntity extends StandardIntegerIdentifiable, HasName {

	EntityType<TestEntity> T = EntityTypes.T(TestEntity.class);

	String getProperty1();
	void setProperty1(String property1);

	String getProperty2();
	void setProperty2(String property2);

	TestEntity getParentEntity();
	void setParentEntity(TestEntity parentEntity);

	Object getObjectProperty();
	void setObjectProperty(Object objectProperty);

	// ###################################################
	// ## . . . . . . Collection properties . . . . . . ##
	// ###################################################

	Set<TestEntity> getSomeSet();
	void setSomeSet(Set<TestEntity> someSet);

	List<TestEntity> getSomeList();
	void setSomeList(List<TestEntity> someList);

	Map<TestEntity, TestEntity> getSomeMap();
	void setSomeMap(Map<TestEntity, TestEntity> someMap);

	Set<Integer> getIntSet();
	void setIntSet(Set<Integer> intSet);

	List<Integer> getIntList();
	void setIntList(List<Integer> intList);

	Map<Integer, Integer> getIntMap();
	void setIntMap(Map<Integer, Integer> intMap);

}
