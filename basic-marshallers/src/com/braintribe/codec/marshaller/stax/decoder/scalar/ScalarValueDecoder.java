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
package com.braintribe.codec.marshaller.stax.decoder.scalar;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.DecodingContext;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactoryContext;

public abstract class ScalarValueDecoder extends Decoder {
	private StringBuilder builder = new StringBuilder();
	private boolean nullAware = false;
	
	public ScalarValueDecoder(boolean nullAware) {
		this.nullAware = nullAware;
	}
	
	public ScalarValueDecoder() {
	}
	
	@Override
	public void end() throws MarshallException {
		String text = builder.toString();
		
		if (nullAware && text.isEmpty() && elementName.equals("null"))
			parent.notifyValue(this, null);
		else { 
			Object value = decode(decodingContext, text);
			parent.notifyValue(this, value);
		}
	}
	
	protected abstract Object decode(DecodingContext context, String text) throws MarshallException;
	
	@Override
	public void appendCharacters(char[] characters, int s, int l) {
		builder.append(characters, s, l);
	}
	@Override
	public Decoder newDecoder(DecoderFactoryContext context, String _elementName, Attributes attributes) throws MarshallException {
		throw new MarshallException("no child element " + _elementName + " is allowed for scalar value elements");
	}
}
