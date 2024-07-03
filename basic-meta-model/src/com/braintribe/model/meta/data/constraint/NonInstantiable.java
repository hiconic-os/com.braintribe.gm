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
package com.braintribe.model.meta.data.constraint;


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.PredicateErasure;

/**
 * This metadata can be used to disable entity instantiation completely.
 * 
 * 
 * If set to <code>true</code>, entity instantiation will not be allowed for the entity type. This setting is stronger than other
 * instantiation related metadata (e.g.;@link FreeEntityInstantiation}, ;@link PropertyCreateEntities}).<br/>
 * Please note though that this property is only used to <b>disable</b> entity instantiation, i.e. setting it to <code>false</code> has no
 * effect which means that in that case the other instantiation related metadata are checked.
 * 
 * @author michael.lafite
 */

public interface NonInstantiable extends Instantiable, PredicateErasure {

	EntityType<NonInstantiable> T = EntityTypes.T(NonInstantiable.class);

}
