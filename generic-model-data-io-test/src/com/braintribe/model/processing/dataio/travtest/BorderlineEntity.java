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
package com.braintribe.model.processing.dataio.travtest;

import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;


public interface BorderlineEntity extends SubBorderlineEntity {

	final EntityType<BorderlineEntity> T = EntityTypes.T(BorderlineEntity.class);

	String getPropertyX();
	void setPropertyX(String propertyX);

	String getPropertyY();
	void setPropertyY(String propertyY);

	Set<String> getSetProperty1();
	void setSetProperty1(Set<String> setProperty1);

	Set<String> getSetProperty2();
	void setSetProperty2(Set<String> setProperty2);
	
	String getAbsentProperty1();
	void setAbsentProperty1(String absentProperty1);

	String getAbsentProperty2();
	void setAbsentProperty2(String absentProperty2);

}
