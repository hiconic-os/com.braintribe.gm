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
package com.braintribe.model.processing.session.impl.persistence.eagerloader.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
@ToStringInformation("${name}")
public interface EagerOwner extends GenericEntity {

	EntityType<EagerOwner> T = EntityTypes.T(EagerOwner.class);

	String getName();
	void setName(String name);

	EagerItem getEntity();
	void setEntity(EagerItem entity);

	List<String> getStringList();
	void setStringList(List<String> stringList);

	Set<String> getStringSet();
	void setStringSet(Set<String> stringSet);

	Map<Integer, String> getIntegerStringMap();
	void setIntegerStringMap(Map<Integer, String> integerStringMap);

	List<EagerItem> getEntityList();
	void setEntityList(List<EagerItem> entityList);

	Set<EagerItem> getEntitySet();
	void setEntitySet(Set<EagerItem> entitySet);

	Map<EagerItem, EagerItem> getEntityMap();
	void setEntityMap(Map<EagerItem, EagerItem> entityMap);

}
