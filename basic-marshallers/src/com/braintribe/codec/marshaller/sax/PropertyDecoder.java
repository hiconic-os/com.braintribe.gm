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

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;

class PropertyDecoder extends Decoder {
	public String propertyName;
	public Object value;
	public boolean absent;
	
	@Override
	public void begin(DecodingContext context, Attributes attributes)
			throws MarshallException {
		absent = Boolean.parseBoolean(attributes.getValue("absent"));
		propertyName = attributes.getValue("name");
	}
	
	@Override
	public void end(DecodingContext context) throws MarshallException {
	}
	
	
	@Override
	public void onDescendantEnd(DecodingContext context, Decoder decoder)
			throws MarshallException {
		this.value = ((ValueDecoder)decoder).getValue(context);
	}
	
	@Override
	public void appendCharacters(char[] characters, int s, int l) {
		// TODO Auto-generated method stub
		
	}
}
