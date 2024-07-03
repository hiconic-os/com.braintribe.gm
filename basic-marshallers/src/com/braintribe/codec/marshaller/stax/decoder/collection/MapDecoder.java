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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactoryContext;
import com.braintribe.codec.marshaller.stax.factory.ObjectDecoderFactory;

public class MapDecoder extends Decoder {
	private Map<Object, Object> map;
	private DecoderFactory keyDecoderFactory;
	private DecoderFactory valueDecoderFactory;
	
	public MapDecoder(DecoderFactory keyDecoderFactory, DecoderFactory valueDecoderFactory) {
		this.keyDecoderFactory = keyDecoderFactory;
		this.valueDecoderFactory = valueDecoderFactory;
	}
	
	public MapDecoder() {
		this.keyDecoderFactory = ObjectDecoderFactory.INSTANCE;
		this.valueDecoderFactory = ObjectDecoderFactory.INSTANCE;
	}

	@Override
	public Decoder newDecoder(DecoderFactoryContext context, String _elementName, Attributes attributes) throws MarshallException {
		if (map == null)
			map = new HashMap<Object, Object>();
		return new MapEntryDecoder(map, keyDecoderFactory, valueDecoderFactory);
	}
	
	@Override
	public void end() throws MarshallException {
		if (map == null) {
			if (elementName.equals("null"))
				parent.notifyValue(this, null);
			else
				parent.notifyValue(this, new HashMap<Object, Object>());
		}
		else
			parent.notifyValue(this, map);
	}
}
