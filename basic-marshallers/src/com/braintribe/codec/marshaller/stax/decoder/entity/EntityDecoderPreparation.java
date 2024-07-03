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

import java.util.HashMap;
import java.util.Map;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.DecodingContext;
import com.braintribe.codec.marshaller.stax.PropertyAbsenceHelper;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.BooleanDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.DateDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.DecimalDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.DoubleDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.EntityReferenceDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.EnumDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.FloatDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.IntegerDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.ListDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.LongDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.MapDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.ObjectDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.SetDecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.StringDecoderFactory;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public class EntityDecoderPreparation {
	
	public EntityType<?> entityType;
	private Map<String, PropertyDecoderPreparation> propertyDecoderPreparations = new HashMap<String, PropertyDecoderPreparation>();
	
	public EntityDecoderPreparation(EntityType<?> entityType) throws MarshallException {
		this.entityType = entityType;
		for (Property property: entityType.getProperties()) {
			DecoderFactory decoderFactory = acquireDecoderFactory(property.getType());
			PropertyDecoderPreparation propertyDecoderPreparation = new PropertyDecoderPreparation(property, decoderFactory);
			propertyDecoderPreparation.property = property;
			propertyDecoderPreparations.put(property.getName(), propertyDecoderPreparation);
		}
		
	}
	
	private static DecoderFactory acquireDecoderFactory(GenericModelType type) throws MarshallException {
		switch (type.getTypeCode()) {
		// simple scalar types
		case booleanType: return BooleanDecoderFactory.INSTANCE;
		case dateType: return DateDecoderFactory.INSTANCE;
		case decimalType: return DecimalDecoderFactory.INSTANCE;
		case doubleType: return DoubleDecoderFactory.INSTANCE;
		case floatType: return FloatDecoderFactory.INSTANCE;
		case integerType: return IntegerDecoderFactory.INSTANCE;
		case longType: return LongDecoderFactory.INSTANCE;
		case stringType: return StringDecoderFactory.INSTANCE;
		
		// collections
		case listType: return new ListDecoderFactory(acquireDecoderFactory(((CollectionType)type).getCollectionElementType()));
		case setType: return new SetDecoderFactory(acquireDecoderFactory(((CollectionType)type).getCollectionElementType()));
		case mapType: 
			GenericModelType[] parameterization = ((CollectionType)type).getParameterization();
			return new MapDecoderFactory(
				acquireDecoderFactory(parameterization[0]),
				acquireDecoderFactory(parameterization[1]));
			
		// custom types
		case entityType: return EntityReferenceDecoderFactory.INSTANCE;
		case enumType: return new EnumDecoderFactory((EnumType)type);

		// object type
		case objectType: return ObjectDecoderFactory.INSTANCE;
		
		default:
			throw new MarshallException("unsupported GenericModelType " + type);
		}
	}
	
	public Decoder newPropertyDecoder(DecodingContext decodingContext, GenericEntity entity, PropertyAbsenceHelper propertyAbsenceHelper, String propertyName) throws MarshallException {
		PropertyDecoderPreparation decoderPreparation = propertyDecoderPreparations.get(propertyName);
		
		if (decoderPreparation == null) {
			if (decodingContext.getDecodingLenience().isPropertyLenient())
				return new LenientDecoder();
			else
				throw new MarshallException("unkown property " + propertyName + " for " + entityType);
		}
		else
			return decoderPreparation.newDecoder(entity, propertyAbsenceHelper);
	}
}
