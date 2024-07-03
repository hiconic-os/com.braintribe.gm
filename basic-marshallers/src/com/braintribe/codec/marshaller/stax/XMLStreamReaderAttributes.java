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
package com.braintribe.codec.marshaller.stax;

import javax.xml.stream.XMLStreamReader;

import org.xml.sax.Attributes;

public class XMLStreamReaderAttributes implements Attributes {

	private XMLStreamReader reader;
	
	public XMLStreamReaderAttributes(XMLStreamReader reader) {
		super();
		this.reader = reader;
	}


	@Override
	public int getLength() {
		return reader.getAttributeCount();
	}

	@Override
	public String getURI(int index) {
		return reader.getAttributeNamespace(index);
	}

	@Override
	public String getLocalName(int index) {
		return reader.getAttributeLocalName(index);
	}

	@Override
	public String getQName(int index) {
		return reader.getAttributePrefix(index) + reader.getAttributeLocalName(index);
	}

	@Override
	public String getType(int index) {
		return reader.getAttributeType(index);
	}

	@Override
	public String getValue(int index) {
		return reader.getAttributeValue(index);
	}

	@Override
	public int getIndex(String uri, String localName) {
		int count = reader.getAttributeCount();
		for (int i = 0; i < count; i++) {
			String ns = reader.getAttributeNamespace(i);
			String name = reader.getAttributeLocalName(i);
			if (ns.equals(uri) && name.equals(localName))
				return i;
		}
		
		return -1;
	}

	@Override
	public int getIndex(String qName) {
		int count = reader.getAttributeCount();
		for (int i = 0; i < count; i++) {
			String name = /*reader.getAttributePrefix(i) + */reader.getAttributeLocalName(i);
			if (name.equals(qName))
				return i;
		}
		
		return -1;
	}

	@Override
	public String getType(String uri, String localName) {
		int i = getIndex(uri, localName);
		return i != -1? reader.getAttributeType(i): null;
	}

	@Override
	public String getType(String qName) {
		int i = getIndex(qName);
		return i != -1? reader.getAttributeType(i): null;
	}

	@Override
	public String getValue(String uri, String localName) {
		int i = getIndex(uri, localName);
		return i != -1? reader.getAttributeValue(i): null;
	}

	@Override
	public String getValue(String qName) {
		int i = getIndex(qName);
		return i != -1? reader.getAttributeValue(i): null;
	}

}
