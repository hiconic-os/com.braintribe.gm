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
package com.braintribe.codec.marshaller.sax;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;

class RequiredTypesDecoder extends ValueDecoder {
	private Set<String> requiredTypes = null;
	
	@Override
	public void begin(DecodingContext context, Attributes attributes) throws MarshallException {
		requiredTypes = new HashSet<String>();
	}
	
	@Override
	public void end(DecodingContext context) throws MarshallException {
		Consumer<Set<String>> requiredTypesReceiver = context.getRequiredTypesReceiver();
		if (requiredTypesReceiver != null) {
			try  {
				requiredTypesReceiver.accept(requiredTypes);
			}
			catch (Exception e) {
				throw new MarshallException("error while propagating required types to configured receiver", e);
			}
		}
	}
	
	@Override
	public void appendCharacters(char[] characters, int s, int l) {
	}
	
	@Override
	public Object getValue(DecodingContext context) {
		return null;
	}
	
	@Override
	public void onDescendantEnd(DecodingContext context, Decoder decoder) throws MarshallException {
		if (decoder instanceof TypeDecoder) {
			TypeDecoder typeDecoder = (TypeDecoder) decoder;
			String typeSignature = (String)typeDecoder.getValue(context);
			requiredTypes.add(typeSignature);
		}
		
	}
}
