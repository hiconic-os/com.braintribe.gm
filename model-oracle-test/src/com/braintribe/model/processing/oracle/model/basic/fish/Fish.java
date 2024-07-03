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
package com.braintribe.model.processing.oracle.model.basic.fish;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.processing.oracle.model.basic.animal.Animal;
import com.braintribe.model.processing.oracle.model.basic.mammal.Dog;
import com.braintribe.model.processing.oracle.model.extended.Mutant;

/**
 * @author peter.gazdik
 */
public interface Fish extends Animal {

	EntityType<Fish> T = EntityTypes.T(Fish.class);

	/** So that we have same property as defined by {@link Dog}, which is then merged in {@link Mutant} entity. */
	String getBreed();
	void setBreed(String value);

}
