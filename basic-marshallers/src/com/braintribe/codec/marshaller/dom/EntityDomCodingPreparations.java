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
package com.braintribe.codec.marshaller.dom;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.dom.coder.DomCoder;
import com.braintribe.codec.marshaller.dom.coder.DomCoders;
import com.braintribe.codec.marshaller.dom.coder.collection.ListDomCoder;
import com.braintribe.codec.marshaller.dom.coder.collection.MapDomCoder;
import com.braintribe.codec.marshaller.dom.coder.collection.SetDomCoder;
import com.braintribe.codec.marshaller.dom.coder.entity.EntityDomCodingPreparation;
import com.braintribe.codec.marshaller.dom.coder.entity.PropertyDomCodingPreparation;
import com.braintribe.codec.marshaller.dom.coder.scalar.EnumDomCoder;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public class EntityDomCodingPreparations {
	private Map<EntityType<?>, EntityDomCodingPreparation> preparations = new ConcurrentHashMap<EntityType<?>, EntityDomCodingPreparation>();
	
	
	public EntityDomCodingPreparation acquireEntityDomCodingPreparation(EntityType<?> entityType) throws CodecException {
		EntityDomCodingPreparation preparation = preparations.get(entityType);
		
		if (preparation == null) {
			List<Property> properties = entityType.getProperties();
			
			PropertyDomCodingPreparation propertyPreparations[] = new PropertyDomCodingPreparation[properties.size()];
			int index = 0;
			for (Property property: properties) {
				@SuppressWarnings("unchecked")
				DomCoder<Object> domCoder = (DomCoder<Object>) acquireDomCoder(property.getType());
				PropertyDomCodingPreparation propertyPreparation = new PropertyDomCodingPreparation();
				propertyPreparation.property = property;
				propertyPreparation.valueCoder = domCoder; 
				propertyPreparations[index++] = propertyPreparation;
			}
			preparation = new EntityDomCodingPreparation(propertyPreparations);
			preparations.put(entityType, preparation);
		}
		
		return preparation;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private DomCoder<?> acquireDomCoder(GenericModelType type) throws CodecException {
		switch (type.getTypeCode()) {
		// simple scalar types
		case stringType: return DomCoders.stringCoder;
		case booleanType: return DomCoders.booleanCoder; 
		case dateType: return DomCoders.dateCoder;
		case decimalType: return DomCoders.decimalCoder;
		case doubleType: return DomCoders.doubleCoder;
		case floatType: return DomCoders.floatCoder;
		case integerType: return DomCoders.integerCoder;
		case longType: return DomCoders.longCoder;
		
		// custom types
		case enumType: return new EnumDomCoder((EnumType<?>) type);
		case entityType: return DomCoders.entityReferenceCoder;

		// object type
		case objectType: return DomCoders.objectCoder;
		
		// collection types
		case mapType: 
			GenericModelType[] parameterization = ((CollectionType)type).getParameterization();
			
			return new MapDomCoder<Object, Object>(
				(DomCoder<Object>)acquireDomCoder(parameterization[0]),
				(DomCoder<Object>)acquireDomCoder(parameterization[1]), true);
			
		case setType:			
			return new SetDomCoder<Object>(
				(DomCoder<Object>)acquireDomCoder(((CollectionType)type).getCollectionElementType()), true);
		case listType: 
			return new ListDomCoder<Object>(
					(DomCoder<Object>)acquireDomCoder(((CollectionType)type).getCollectionElementType()), true);
		default:
			throw new CodecException("unsupported GenericModelType " + type);
		} 
	}


}
