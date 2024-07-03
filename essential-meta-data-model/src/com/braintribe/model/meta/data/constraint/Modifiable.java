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
import com.braintribe.model.meta.data.Predicate;
import com.braintribe.model.meta.data.UniversalMetaData;

/**
 * Controls whether a user can change the value of given property. There are three different cases we might want to achieve:
 * 
 * <ul>
 * <li><b>Fully modifiable</b> - user can do changes at any time - <tt>Modifiable</tt> must resolve to <tt>true</tt>.
 * <li><b>Fully non-modifiable</b> - user cannot do changes at any time - <tt>Modifiable</tt> must resolve to <tt>false</tt>. Used for properties
 * meant to be set automatically by the system, e.g. some value computed based on other properties.
 * <li><b>Modifiable on creation only</b> - user can set the initial value but not edit existing instances - Both <tt>Modifiable</tt> and
 * {@link Mandatory} must resolve to <tt>true</tt>.
 * </ul>
 * 
 * Erasure is {@link Unmodifiable}.
 */
public interface Modifiable extends UniversalMetaData, Predicate {

	EntityType<Modifiable> T = EntityTypes.T(Modifiable.class);

}
