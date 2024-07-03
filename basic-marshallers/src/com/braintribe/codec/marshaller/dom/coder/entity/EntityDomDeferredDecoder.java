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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.dom.DomDecodingContext;
import com.braintribe.codec.marshaller.dom.EntityRegistration;
import com.braintribe.codec.marshaller.dom.TypeInfo;
import com.braintribe.codec.marshaller.dom.coder.DeferredDecoder;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;

public class EntityDomDeferredDecoder extends DeferredDecoder {

	private EntityType<?> entityType;
	private EntityDomCodingPreparation preparation;
	public GenericEntity entity;
	public String refId;
	public TypeInfo typeInfo;
	public Element element;
	
	public EntityDomDeferredDecoder(EntityType<?> entityType, EntityDomCodingPreparation preparation, Element element) {
		this.entityType = entityType;
		this.preparation = preparation;
		this.element = element;
	}
	
	@Override
	public boolean continueDecode(DomDecodingContext context) throws CodecException {
		String id = element.getAttribute("id");
		EntityRegistration registration = context.acquireEntity(id);
		GenericEntity _entity = registration.entity;
		
		PropertyAbsenceHelper propertyAbsenceHelper = context.providePropertyAbsenceHelper();
		
		Node node = element.getFirstChild();
		
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element propertyElement = (Element)node;
				String propertyName = propertyElement.getAttribute("p");
				PropertyDomCodingPreparation propertyPreparation = preparation.getPropertyCoderByName(propertyName);
				if (preparation != null) {
					propertyAbsenceHelper.addPresent(propertyPreparation.property);
					if (propertyElement.getTagName().equals("a")) {
						AbsenceInformation ai = decodeAbsenceInforamtion(context, propertyElement);
						if (ai != null) {
							// PGA: I am adding the null check because of incompatible changes. I do not know if 'ai' can every be null
							propertyPreparation.property.setAbsenceInformation(_entity, ai);
						}
					}
					else {
						Object value = propertyPreparation.valueCoder.decode(context, propertyElement);
						propertyPreparation.property.setDirectUnsafe(_entity, value);
					}
				}
				else 
					throw new CodecException("unkown property " + propertyName + " for type " + entityType);
			}
			node = node.getNextSibling();
		}
		
		propertyAbsenceHelper.ensureAbsenceInformation(entityType, _entity);
		return false;
	}
	
	private static AbsenceInformation decodeAbsenceInforamtion(DomDecodingContext context, Element element) throws CodecException {
		String text = element.getTextContent();
		if (text.isEmpty())
			return context.getAbsenceInformationForMissingProperties();
		else
			return (AbsenceInformation)context.acquireEntity(text).entity;
	}
	
}
