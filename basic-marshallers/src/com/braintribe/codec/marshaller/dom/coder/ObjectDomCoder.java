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
package com.braintribe.codec.marshaller.dom.coder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.dom.DomDecodingContext;
import com.braintribe.codec.marshaller.dom.DomEncodingContext;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;

public class ObjectDomCoder implements DomCoder<Object> {

	@Override
	public Object decode(DomDecodingContext context, Element element) throws CodecException {
		String tagName = element.getTagName();
		
		if (tagName.length() > 1)
			throw new CodecException("Unsupported element type "+tagName);
		
		switch (tagName.charAt(0)) {
		// simple values
		case 'b': return DomCoders.booleanCoder.decode(context, element);
		case 's': return DomCoders.stringCoder.decode(context, element);
		case 'i': return DomCoders.integerCoder.decode(context, element);
		case 'l': return DomCoders.longCoder.decode(context, element);
		case 'f': return DomCoders.floatCoder.decode(context, element);
		case 'd': return DomCoders.doubleCoder.decode(context, element);
		case 'D': return DomCoders.decimalCoder.decode(context, element);
		case 'T': return DomCoders.dateCoder.decode(context, element);
		
		// collections
		case 'L': return DomCoders.listCoder.decode(context, element);
		case 'S': return DomCoders.setCoder.decode(context, element);
		case 'M': return DomCoders.mapCoder.decode(context, element);
		
		// null
		case 'n': return null;
		
		// custom types
		case 'e': return DomCoders.enumCoder.decode(context, element);
		case 'r': return DomCoders.entityReferenceCoder.decode(context, element);
		
		default: 
			throw new CodecException("Unsupported element type "+tagName);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Element encode(DomEncodingContext context, Object value) throws CodecException {
		if (value == null) {
			return context.getDocument().createElement("n");
		}
		else {
			GenericModelType type = GMF.getTypeReflection().getType(value);
			
			switch (type.getTypeCode()) {
			// simple scalar types
			case booleanType: return DomCoders.booleanCoder.encode(context, (Boolean)value);
			case dateType: return DomCoders.dateCoder.encode(context, (Date)value);
			case decimalType: return DomCoders.decimalCoder.encode(context, (BigDecimal)value);
			case doubleType: return DomCoders.doubleCoder.encode(context, (Double)value);
			case floatType: return DomCoders.floatCoder.encode(context, (Float)value);
			case integerType: return DomCoders.integerCoder.encode(context, (Integer)value);
			case longType: return DomCoders.longCoder.encode(context, (Long)value);
			case stringType: return DomCoders.stringCoder.encode(context, (String)value);
			
			// custom types
			case entityType: return DomCoders.entityReferenceCoder.encode(context, (GenericEntity)value);
			case enumType: return DomCoders.enumCoder.encode(context, value);

			// collections
			case listType: return DomCoders.listCoder.encode(context, (List<Object>)value);
			case mapType: return DomCoders.mapCoder.encode(context, (Map<Object,Object>)value);
			case setType: return DomCoders.setCoder.encode(context, (Set<Object>)value);
				
			default:
				throw new CodecException("unsupported GenericModelType " + type);
			}
		}
	}
}
