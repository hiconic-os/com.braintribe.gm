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
package com.braintribe.codec.marshaller.stax.v4.decoder.entity;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.EntityRegistration;
import com.braintribe.codec.marshaller.stax.PropertyAbsenceHelper;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactoryContext;
import com.braintribe.codec.marshaller.stax.v4.decoder.ValueHostingDecoder;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

public class EntityDecoder extends ValueHostingDecoder {
	private GenericEntity entity;
	private PropertyAbsenceHelper propertyAbsenceHelper;
	private EntityType<?> entityType;
	
	public EntityDecoder() {
		this.propertyDecorated = true;
	}
	
	@Override
	public Decoder newDecoder(DecoderFactoryContext context, String _elementName, Attributes attributes) throws MarshallException {
		return super.newDecoder(context, _elementName, attributes);
	}
	
	@Override
	public void notifyValue(Decoder origin, Object value) throws MarshallException {
		if (entity == null)
			return;
		
		Property property = entityType.findProperty(origin.propertyName);
		
		if (property != null) {
			if (origin.elementName.charAt(0) == 'a') {
				property.setAbsenceInformation(entity, (AbsenceInformation)value);
			}
			else {
				try {
					property.setDirectUnsafe(entity, value);
				}
				catch (ClassCastException e) {
					if (!decodingContext.getDecodingLenience().isPropertyClassCastExceptionLenient())
						throw e;
				}
			}
		}
		else if (!decodingContext.getDecodingLenience().isPropertyLenient())
			throw new MarshallException("unknown property: " + entityType.getTypeSignature() + "." + propertyName);
	}
	
	@Override
	public void begin(Attributes attributes) throws MarshallException {
		try {
			String id = attributes.getValue("id");
			EntityRegistration registration = decodingContext.acquireEntity(id);
			if (registration.typeInfo != null) {
				entity = registration.entity;
				entityType = (EntityType<?>)registration.typeInfo.type;
				propertyAbsenceHelper = decodingContext.providePropertyAbsenceHelper();
			}
		}
		catch (Exception e) {
			throw new MarshallException("error while decoding entity", e);
		}
	}
	
	@Override
	public void end() throws MarshallException {
		if (propertyAbsenceHelper != null)
			propertyAbsenceHelper.ensureAbsenceInformation(entityType, entity);
		
		parent.notifyValue(this, entity);
	}

	
}
