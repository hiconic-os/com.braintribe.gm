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
package com.braintribe.model.processing.traversing.engine.impl.clone;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.traversing.engine.api.customize.PropertyTransferContext;
import com.braintribe.model.processing.traversing.engine.api.customize.PropertyTransferExpert;

/**
 * @author peter.gazdik
 */
public class BasicPropertyTransferExpert implements PropertyTransferExpert {

	public static final BasicPropertyTransferExpert INSTANCE = new BasicPropertyTransferExpert();

	private BasicPropertyTransferExpert() {
	}

	@Override
	public void transferProperty(GenericEntity clonedEntity, Property property, Object clonedValue, PropertyTransferContext context) {
		property.set(clonedEntity, clonedValue);
	}

}
