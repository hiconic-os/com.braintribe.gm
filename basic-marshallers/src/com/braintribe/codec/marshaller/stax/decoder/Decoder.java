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
package com.braintribe.codec.marshaller.stax.decoder;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.DecodingContext;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactory;

public abstract class Decoder extends DecoderFactory {
	public Decoder parent;
	public DecodingContext decodingContext;
	public String elementName;
	public String propertyName;
	
	/**
	 * @param characters  
	 * @param s 
	 * @param l 
	 */
	public void appendCharacters(char [] characters, int s, int l) {}
	/**
	 * @param attributes  
	 * @throws MarshallException 
	 */
	public void begin(Attributes attributes) throws MarshallException {}
	/**
	 * @throws MarshallException  
	 */
	public void end() throws MarshallException {}
	
	
	/**
	 * @param origin  
	 * @param value 
	 */
	public void notifyValue(Decoder origin, Object value) throws MarshallException {
		parent.notifyValue(this, value);
	}
	
	/**
	 * @param origin  
	 * @param referenceId 
	 */
	public void notifyForwardEntity(Decoder origin, final String referenceId) throws MarshallException {
		parent.notifyForwardEntity(this, referenceId);
	}
}
