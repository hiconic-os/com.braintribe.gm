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
package com.braintribe.model.processing.manipulator.expert.basic;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.manipulator.api.Manipulator;
import com.braintribe.model.processing.manipulator.api.ManipulatorContext;

public class ChangeValueManipulator implements Manipulator<ChangeValueManipulation> {

	public static final ChangeValueManipulator defaultInstance = new ChangeValueManipulator();

	@Override
	public void apply(ChangeValueManipulation manipulation, ManipulatorContext context) {
		LocalEntityProperty owner = context.resolveOwner(manipulation);

		GenericEntity entity = owner.getEntity();
		String propertyName = owner.getPropertyName();

		Property property = entity.entityType().getProperty(propertyName);
		property.set(entity, context.resolveValue(property.getType(), manipulation.getNewValue()));
	}
}
