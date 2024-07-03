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
package model;

/**
 * @author peter.gazdik
 */
public interface Person_aop extends Person, GenericEntity_aop {
	
	@Override
	default public String getName() {
		return (String)read(Person_Name_Property.INSTANCE);
	}

	@Override
	default void setName(String value) {
		write(Person_Name_Property.INSTANCE, value);
	}
	
	@Override
	default long getCount() {
		return (Long) read(Person_Count_Property.INSTANCE);
	}
	
	@Override
	default void setCount(long value) {
		write(Person_Count_Property.INSTANCE, value);
	}
	
	@Override
	default void setFather(Person value) {
		write(Person__FATHER.INSTANCE, value);
	}

	@Override
	default Person getFather() {
		return (Person) read(Person__FATHER.INSTANCE);
	}

}

