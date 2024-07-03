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
package com.braintribe.codec.marshaller.sax;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

class ActivePropertyAbsenceHelper extends PropertyAbsenceHelper {
	private Set<Property> presentProperties = new HashSet<Property>();
	private DecodingContext context;
	
	public ActivePropertyAbsenceHelper(DecodingContext context) {
		super();
		this.context = context;
	}

	@Override
	public void addPresent(Property property) {
		presentProperties.add(property);
	}
	
	@Override
	public void ensureAbsenceInformation(EntityType<?> entityType,
			GenericEntity entity) {
		List<Property> properties = entityType.getProperties();
		
		if (properties.size() != presentProperties.size()) {
			for (Property property: properties) {
				if (!presentProperties.contains(property)) {
					property.setAbsenceInformation(entity, context.getAbsenceInformationForMissingProperties());
				}
			}
		}
		
	}
}
