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

import java.util.HashSet;
import java.util.Set;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.v4.decoder.ValueHostingDecoder;

public class SetDecoder extends ValueHostingDecoder {
	private Set<Object> set = new HashSet<Object>();
	
	@Override
	public void end() throws MarshallException {
		parent.notifyValue(this, set);
	}
	
	@Override
	public void notifyValue(Decoder origin, Object value) {
		set.add(value);
	}

}
