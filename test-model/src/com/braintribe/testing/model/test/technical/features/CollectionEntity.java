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
package com.braintribe.testing.model.test.technical.features;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * An entity with various collection type properties.
 *
 * @author michael.lafite
 */

public interface CollectionEntity extends GenericEntity {

	EntityType<CollectionEntity> T = EntityTypes.T(CollectionEntity.class);

	List<String> getStringList();
	void setStringList(List<String> stringList);

	List<Integer> getIntegerList();
	void setIntegerList(List<Integer> integerList);

	List<BigDecimal> getDecimalList();
	void setDecimalList(List<BigDecimal> decimalList);

	List<SimpleEntity> getSimpleEntityList();
	void setSimpleEntityList(List<SimpleEntity> simpleEntityList);

	Set<String> getStringSet();
	void setStringSet(Set<String> stringSet);

	Set<Integer> getIntegerSet();
	void setIntegerSet(Set<Integer> integerSet);

	Set<BigDecimal> getDecimalSet();
	void setDecimalSet(Set<BigDecimal> decimalSet);

	Set<SimpleEntity> getSimpleEntitySet();
	void setSimpleEntitySet(Set<SimpleEntity> simpleEntitySet);

	Map<String, String> getStringToStringMap();
	void setStringToStringMap(Map<String, String> stringToStringMap);

	Map<Integer, Integer> getIntegerToIntegerMap();
	void setIntegerToIntegerMap(Map<Integer, Integer> integerToIntegerMap);

	Map<String, SimpleEntity> getStringToSimpleEntityMap();
	void setStringToSimpleEntityMap(Map<String, SimpleEntity> stringToSimpleEntityMap);

	Map<SimpleEntity, String> getSimpleEntityToStringMap();
	void setSimpleEntityToStringMap(Map<SimpleEntity, String> simpleEntityToStringMap);

	Map<SimpleEntity, ComplexEntity> getSimpleEntityToComplexEntityMap();
	void setSimpleEntityToComplexEntityMap(Map<SimpleEntity, ComplexEntity> simpleEntityToComplexEntityMap);

}
