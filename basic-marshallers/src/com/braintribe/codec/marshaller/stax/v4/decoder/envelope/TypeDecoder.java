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
package com.braintribe.codec.marshaller.stax.v4.decoder.envelope;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.DecodingContext;
import com.braintribe.codec.marshaller.stax.TypeInfo4Read;
import com.braintribe.codec.marshaller.stax.v4.decoder.scalar.ScalarValueDecoder;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;

public class TypeDecoder extends ScalarValueDecoder {
	private TypeInfo4Read typeInfo;
	private boolean isEntityType;
	
	@Override
	public void begin(Attributes attributes) throws MarshallException {
		super.begin(attributes);
		typeInfo = new TypeInfo4Read();
		
		
		String alias = attributes.getValue("alias");
		String as = attributes.getValue("as");
		
		typeInfo.alias = alias;
		typeInfo.as = (as != null && !as.isEmpty())? as: alias; 
		 
		String encodedNum = attributes.getValue("num");
		if (encodedNum != null) {
			typeInfo.setCount(Integer.parseInt(encodedNum));
			isEntityType = true;
		}
	}
	
	@Override
	protected Object decode(DecodingContext context, String text)
			throws MarshallException {
		typeInfo.typeSignature = text;
		if (isEntityType) {
			EntityType<?> entityType = context.findType(text);
			
			if (entityType != null) {
				typeInfo.type = entityType;
			}
			else if (!context.getDecodingLenience().isTypeLenient()) {
				throw new MarshallException("unable to decode unkown type: " + text);
			}
		}
		else {
			EnumType<?> enumType = context.findType(text);
			
			if (enumType != null) {
				typeInfo.type = enumType;
			}
			else if (!context.getDecodingLenience().isTypeLenient()) {
				throw new MarshallException("unable to decode unkown type: " + text);
			}
		}
		
		return typeInfo;
	}
}
