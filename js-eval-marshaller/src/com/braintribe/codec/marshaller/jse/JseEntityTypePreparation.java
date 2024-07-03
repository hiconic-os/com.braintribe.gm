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
package com.braintribe.codec.marshaller.jse;

import java.util.List;

import com.braintribe.codec.marshaller.jse.tree.JsePoolAddress;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

public class JseEntityTypePreparation {
	public EntityType<?> type;
	public JsePoolAddress address;
	public JsePropertyPreparation[] preparations;
	private JseEncodingContext context;
	
	public JseEntityTypePreparation(JseEncodingContext context, EntityType<?> type, JsePoolAddress address) {
		this.context = context;
		this.address = address;
		List<Property> properties = type.getProperties();
		preparations = new JsePropertyPreparation[properties.size()];
		int i = 0;
		for (Property property: properties) {
			JsePropertyPreparation propertyPreparation = new JsePropertyPreparation(context, address, property);
			propertyPreparation.property = property;
			preparations[i++] = propertyPreparation;
		}
	}
}
