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
package com.braintribe.xml.parser.experts;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

public class StringValueExpert extends AbstractContentExpert {	
	public StringValueExpert() {		
	}

	public StringValueExpert(String property) {
		this.property = property;
	}
		
	@Override
	public void startElement(ContentExpert parent, String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (property == null)
			property = qName;
	}

	@Override
	public void endElement(ContentExpert parent, String uri, String localName, String qName) throws SAXException {	
		parent.attach( this);
	}

	@Override
	public void attach(ContentExpert child) {
	}

	@Override
	public Object getPayload() {
		if (buffer == null) {
			return null;
		}
		return buffer.toString().trim();
	}

	@Override
	public GenericEntity getInstance() {	
		return null;
	}

	@Override
	public EntityType<GenericEntity> getType() {
		return null;
	}

	
}
