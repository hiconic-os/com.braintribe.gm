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
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.data.EntityTypeMetaData;

/**
 * This metadata defines a default property to be used when navigating an entity. For example, when using the Open
 * action, or double clicking an entity, currently we navigate to that entity, showing all its properties. By defining
 * this metadata, we, instead, navigate not to the entity itself, but to the defined property. Of course, the property
 * then must be configured to a non-simple property.
 * 
 * @author michel.docouto
 */
public interface DefaultNavigation extends EntityTypeMetaData {

	EntityType<DefaultNavigation> T = EntityTypes.T(DefaultNavigation.class);

	void setProperty(GmProperty property);
	GmProperty getProperty();

}
