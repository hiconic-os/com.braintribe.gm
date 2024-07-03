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
package com.braintribe.codec.marshaller.stax.decoder.collection;

import java.util.Map;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.EntityRegistrationListener;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactoryContext;
import com.braintribe.model.generic.GenericEntity;

public class MapEntryDecoder extends Decoder {
	private Object key;
	private Object value;
	private Map<Object, Object> map;
	private int partsReceived = 0;
	private int decodersReturned = 0;
	private DecoderFactory keyDecoderFactory;
	private DecoderFactory valueDecoderFactory;
	
	public MapEntryDecoder(Map<Object, Object> map, DecoderFactory keyDecoderFactory, DecoderFactory valueDecoderFactory) {
		super();
		this.map = map;
		this.keyDecoderFactory = keyDecoderFactory;
		this.valueDecoderFactory = valueDecoderFactory;
	}
	
	@Override
	public Decoder newDecoder(DecoderFactoryContext context, String _elementName, Attributes attributes) throws MarshallException {
		switch (decodersReturned++) {
		case 0: return new MapKeyDecoder(keyDecoderFactory);
		case 1: return new MapValueDecoder(valueDecoderFactory);
		default:
			throw new MarshallException("invalid child element count for entry element");
		}
	}
	
	@Override
	public void notifyValue(Decoder origin, Object partValue) {
		MapEntryPartDecoder partDecoder = (MapEntryPartDecoder) origin;
		if (partDecoder.isKeyDecoder) {
			key = partValue;
		}
		else {
			value = partValue;
		}
		
		if (++partsReceived == 2) {
			map.put(key, value);
		}
	}
	
	@Override
	public void notifyForwardEntity(final Decoder origin, String referenceId) {
		decodingContext.addEntityRegistrationListener(referenceId, new EntityRegistrationListener() {
			
			@Override
			public void onEntityRegistered(GenericEntity entity) throws MarshallException {
				notifyValue(origin, entity);
			}
		});
	}
	
}
