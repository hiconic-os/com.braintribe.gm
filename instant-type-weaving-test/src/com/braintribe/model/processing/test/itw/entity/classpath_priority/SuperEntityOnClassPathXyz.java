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
package com.braintribe.model.processing.test.itw.entity.classpath_priority;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.processing.test.itw.ClasspathPriotityItwTests;

/**
 * This is used in {@link ClasspathPriotityItwTests} to create a {@link GmEntityType} with same type signature but different properties as
 * {@link SuperEntityOnClassPath}.
 * 
 * @author peter.gazdik
 */
public interface SuperEntityOnClassPathXyz extends GenericEntity {

	EntityType<SuperEntityOnClassPathXyz> T = EntityTypes.T(SuperEntityOnClassPathXyz.class);

	String getNonCpName();
	void setNonCpName(String nonCpName);

}
