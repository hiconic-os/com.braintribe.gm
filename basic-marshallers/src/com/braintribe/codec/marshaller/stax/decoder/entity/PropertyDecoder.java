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

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.EntityRegistrationListener;
import com.braintribe.codec.marshaller.stax.PropertyAbsenceHelper;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactoryContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;

public class PropertyDecoder extends Decoder {
	public Object value;
	public boolean absent;
	private GenericEntity entity;
	private PropertyAbsenceHelper propertyAbsenceHelper;
	private PropertyDecoderPreparation preparation;
	private Decoder valueDecoder;
	
	public PropertyDecoder(GenericEntity entity, PropertyAbsenceHelper propertyAbsenceHelper, PropertyDecoderPreparation preparation) {
		this.entity = entity;
		this.propertyAbsenceHelper = propertyAbsenceHelper;
		this.preparation = preparation;
	}
	
	public Decoder getValueDecoder() {
		if (valueDecoder == null) {
			
		}
		return valueDecoder;
	}
	
	@Override
	public Decoder newDecoder(DecoderFactoryContext context, String elementName, Attributes attributes) throws MarshallException {
		return preparation.valueDecoderFactory.newDecoder(context, elementName, attributes);
	}
	
	@Override
	public void begin(Attributes attributes)
			throws MarshallException {
		absent = Boolean.parseBoolean(attributes.getValue("absent"));
		propertyAbsenceHelper.addPresent(preparation.property);
	}
	
	@Override
	public void notifyForwardEntity(Decoder origin, String referenceId) {
		decodingContext.addEntityRegistrationListener(referenceId, new EntityRegistrationListener() {
			@Override
			public void onEntityRegistered(GenericEntity entity) throws MarshallException {
				notifyValue(null, entity);
			}
		});
	}
	
	@Override
	public void notifyValue(Decoder origin, Object value) {
		if (absent) {
			preparation.property.setAbsenceInformation(entity, (AbsenceInformation)value);
		}
		else {
			try {
				preparation.property.setDirectUnsafe(entity, value);
			}
			catch (ClassCastException e) {
				if (!decodingContext.getDecodingLenience().isPropertyClassCastExceptionLenient())
					throw e;
			}
		}
	}
}
