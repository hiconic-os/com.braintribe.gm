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
package com.braintribe.model.meta.data.prompt;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.EntityTypeMetaData;

/**
 * This meta data denotes entity types that actually stand for other by wrapping them based on a property they have.
 * This wrapping is normally used to adapt to other type hierarchies. Unfortunately the wrapping leads to a lack of
 * expressiveness. By actually denoting this wrapping a display and editing software like the GME can restore the
 * expressiveness for the user by using delegate editors on the actual wrapped property.
 * 
 * @author dirk.scheffler
 */
public interface Wrapped extends EntityTypeMetaData {

	EntityType<Wrapped> T = EntityTypes.T(Wrapped.class);

	/** the name of the property which holds the actual value that is wrapped by the wrapper type */
	void setWrapperPropertyName(String wrapperPropertyName);
	String getWrapperPropertyName();

}