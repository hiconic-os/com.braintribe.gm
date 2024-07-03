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
package com.braintribe.codec.marshaller.stax.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.DecodingContext;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.decoder.entity.EntityDecoder;
import com.braintribe.codec.marshaller.stax.decoder.entity.EntityDecoderPreparation;
import com.braintribe.codec.marshaller.stax.decoder.entity.LenientDecoder;
import com.braintribe.model.generic.reflection.EntityType;

public class DecoderFactoryContext {
	public Map<String, EntityDecoderPreparation> entityDecoderPreparations = new ConcurrentHashMap<String, EntityDecoderPreparation>();

	public Decoder newEntityDecoder(DecodingContext decodingContext, Attributes attributes) throws MarshallException {
		String typeSignature = attributes.getValue("type");
		
		if (typeSignature != null) {
			EntityDecoderPreparation preparation = entityDecoderPreparations.get(typeSignature);
			
			if (preparation == null) {
				EntityType<?> entityType = decodingContext.findType(typeSignature);

				if (entityType == null) { 
					if (decodingContext.getDecodingLenience().isTypeLenient())
						return new LenientDecoder();
					else
						throw new MarshallException("unable to decode unkown type: " + typeSignature);
				}
				else {
					preparation = new EntityDecoderPreparation(entityType);
					entityDecoderPreparations.put(typeSignature, preparation);
				}
			}
			
			return new EntityDecoder(preparation);
		}
		else 
			throw new MarshallException("entity elements in the pool element need a typeSignature attribute");
	}
}
