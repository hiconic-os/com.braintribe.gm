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
package com.braintribe.codec.marshaller.dom.coder.entity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.dom.DomEncodingContext;
import com.braintribe.codec.marshaller.dom.TypeInfo;
import com.braintribe.codec.marshaller.dom.coder.DeferredEncoder;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.Property;

public class EntityDomDeferredEncoder extends DeferredEncoder {
	private EntityDomCodingPreparation preparation;
	public GenericEntity entity;
	public String refId;
	private TypeInfo typeInfo;


	public EntityDomDeferredEncoder(EntityDomCodingPreparation preparation, TypeInfo typeInfo, GenericEntity entity, String refId) {
		this.preparation = preparation;
		this.entity = entity;
		this.refId = refId;
		this.typeInfo = typeInfo;
	}
	
	@Override
	public boolean continueEncode(DomEncodingContext context) throws CodecException {
		Document document = context.getDocument();
		Element entityElement = document.createElement("E");
		
		entityElement.setAttribute("t", typeInfo.as);
		entityElement.setAttribute("id", refId);
		
		GenericEntity _entity = this.entity;

		for (PropertyDomCodingPreparation propertyPreparation: preparation.propertyPreparations) {
			Property property = propertyPreparation.property;
			
			Object value = property.get(_entity);
			
			Element propertyElement = null;
			
			if (value == null) {
				AbsenceInformation absenceInformation = property.getAbsenceInformation(_entity);
				if (absenceInformation != null && context.shouldWriteAbsenceInformation()) {
					propertyElement = encodeAbsenceInformation(context, absenceInformation);
				} 
			}
			else {
				propertyElement = propertyPreparation.valueCoder.encode(context, value);
			}
			
			if (propertyElement != null) {
				propertyElement.setAttribute("p", property.getName());
				entityElement.appendChild(propertyElement);
			}
		}
		
		context.appendToPool(entityElement);
		
		return false;
	}
	
	
	private static Element encodeAbsenceInformation(DomEncodingContext context, AbsenceInformation absenceInformation) throws CodecException {
		Element element = context.getDocument().createElement("a");
		if (!context.isSimpleAbsenceInformation(absenceInformation)) {
			String text = context.lookupQualifiedId(absenceInformation);
			element.setTextContent(text);
		}
		return element;
	}

}
