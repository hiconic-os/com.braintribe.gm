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
import com.braintribe.codec.marshaller.stax.PropertyAbsenceHelper;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactoryContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

public class EntityDecoder extends Decoder {
	private GenericEntity entity;
	private PropertyAbsenceHelper propertyAbsenceHelper;
	private EntityDecoderPreparation preparation;
	private EntityType<?> entityType;
	
	public EntityDecoder(EntityDecoderPreparation preparation) {
		this.preparation = preparation;
		this.entityType = preparation.entityType;
	}
	
	@Override
	public Decoder newDecoder(DecoderFactoryContext context, String elementName, Attributes attributes) throws MarshallException{
		String name = attributes.getValue("name");
		return preparation.newPropertyDecoder(decodingContext, entity, propertyAbsenceHelper, name);
	}
	
	@Override
	public void end() throws MarshallException {
		if (propertyAbsenceHelper != null)
			propertyAbsenceHelper.ensureAbsenceInformation(entityType, entity);
		
		parent.notifyValue(this, entity);
	}

	@Override
	public void begin(Attributes attributes)
			throws MarshallException {
		try {
			entity = decodingContext.isEnhanced()? entityType.create(): entityType.createPlain();
			propertyAbsenceHelper = decodingContext.providePropertyAbsenceHelper();

			String idString = attributes.getValue("id");
			
			decodingContext.register(entity, idString);
		}
		catch (Exception e) {
			throw new MarshallException("error while decoding entity", e);
		}
	}
}
