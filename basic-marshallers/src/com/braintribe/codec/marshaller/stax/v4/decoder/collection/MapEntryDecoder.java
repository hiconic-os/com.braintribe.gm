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
package com.braintribe.codec.marshaller.stax.v4.decoder.collection;

import java.util.Map;

import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.v4.decoder.ValueHostingDecoder;

public class MapEntryDecoder extends ValueHostingDecoder {
	private Object key;
	private Map<Object, Object> map;
	private int partsReceived = 0;
	
	public MapEntryDecoder(Map<Object, Object> map) {
		super();
		this.map = map;
	}
	
	@Override
	public void notifyValue(Decoder origin, Object partValue) {
		if (++partsReceived == 1) {
			key = partValue;
		}
		else {
			map.put(key, partValue);
		}
	}

}
