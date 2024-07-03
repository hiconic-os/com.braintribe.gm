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
package com.braintribe.codec.marshaller.stax.decoder.entity;

import com.braintribe.codec.marshaller.stax.PropertyAbsenceHelper;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactory;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;

public class PropertyDecoderPreparation {
	public Property property;
	public DecoderFactory valueDecoderFactory;
	
	public PropertyDecoderPreparation(Property property, DecoderFactory valueDecoderFactory) {
		super();
		this.property = property;
		this.valueDecoderFactory = valueDecoderFactory;
	}

	public PropertyDecoder newDecoder(GenericEntity entity, PropertyAbsenceHelper propertyAbsenceHelper) {
		return new PropertyDecoder(entity, propertyAbsenceHelper, this);
	}
}
