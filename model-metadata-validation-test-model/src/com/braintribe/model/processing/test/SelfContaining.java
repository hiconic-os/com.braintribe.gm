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
package com.braintribe.model.processing.test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.Max;
import com.braintribe.model.generic.annotation.meta.MaxLength;
import com.braintribe.model.generic.annotation.meta.Min;
import com.braintribe.model.generic.annotation.meta.MinLength;
import com.braintribe.model.generic.annotation.meta.Pattern;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@ToStringInformation("|${#type_short}: ${name}|")
public interface SelfContaining extends GenericEntity {
	EntityType<SelfContaining> T = EntityTypes.T(SelfContaining.class);

	void setChild(SelfContaining selfContaining);
	SelfContaining getChild();

	void setListOfChildren(List<SelfContaining> list);
	List<SelfContaining> getListOfChildren();

	void setSetOfChildren(Set<SelfContaining> list);
	Set<SelfContaining> getSetOfChildren();

	void setMapOfChildren(Map<SelfContaining, SelfContaining> map);
	Map<SelfContaining, SelfContaining> getMapOfChildren();

	@Mandatory
	@MaxLength(23)
	@MinLength(6)
	@Pattern("child.*")
	String getName();
	void setName(String name);

	@Max("130L")
	@Min("0L")
	Long getAge();
	void setAge(Long age);

	@Deprecated
	@Max("1.2345f")
	float getPoints();
	void setPoints(float points);
}
