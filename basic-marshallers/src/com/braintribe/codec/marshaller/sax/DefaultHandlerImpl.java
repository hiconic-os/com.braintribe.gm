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

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;

public class DefaultHandlerImpl<T> extends DefaultHandler {


	
	private static Decoder getDecoder(String name) throws SAXException {
		switch (name) {
		case "boolean": return new BooleanDecoder();
		case "string": return new StringDecoder();
		case "integer": return new IntegerDecoder();
		case "long": return new LongDecoder();
		case "float": return new FloatDecoder();
		case "double": return new DoubleDecoder();
		case "decimal": return new DecimalDecoder();
		case "date": return new DateDecoder();
		case "entity": return new EntityDecoder();
		case "enum": return new EnumDecoder();
		case "list": return new ListDecoder();
		case "set": return new SetDecoder();
		case "map": return new MapDecoder();
		case "null": return new NullDecoder();
		case "property": return new PropertyDecoder();
		case "entry": return new MapEntryDecoder();
		case "key": return new MapKeyDecoder();
		case "value": return new MapValueDecoder();
		case "gm-data": return new GmDataDecoder();
		case "root-value": return new RootValueDecoder();
		case "pool": return new PoolDecoder();
		case "required-types": return new RequiredTypesDecoder();
		case "type": return new TypeDecoder();
		default: throw new SAXException("Unsupported element type "+name);
		}
	}
	
	private Decoder topDecoder;
	private final DecodingContextImpl<T> context;
	private Decoder firstEndedDecoder;
	private Decoder lastEndedDecoder;
	private Object value;
	
	public DefaultHandlerImpl(boolean createEnhancedEntities, Consumer<Set<String>> requiredTypesReceiver, GmDeserializationOptions options) {
		context = new DecodingContextImpl<>(createEnhancedEntities, requiredTypesReceiver, options);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		try {
			Decoder decoder = getDecoder(qName);
			
			decoder.predecessor = topDecoder;
			
			topDecoder = decoder;
			decoder.begin(context, attributes);
		} catch (Exception e) {
			throw new SAXException("error while resolving/creating decoder", e);
		}
	}
	public Object getValue() {
		return value;
	}
	
	// <foo><bar/></foo>
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (firstEndedDecoder == null) {
			firstEndedDecoder = lastEndedDecoder = topDecoder;
		}
		else {
			lastEndedDecoder.nextToBeEvaluated = topDecoder;
			lastEndedDecoder = topDecoder;
		}
		
		Decoder currentDecoder = topDecoder;
		topDecoder = topDecoder.predecessor;
		
		
		try {
			// if we reached the top again run through the decoder structure and finish
			if (topDecoder == null) {
				currentDecoder = firstEndedDecoder;
				
				while (currentDecoder != null) {
					currentDecoder.end(context);
					Decoder parent = currentDecoder.predecessor;
					if (parent != null)
						parent.onDescendantEnd(context, currentDecoder);
					else {
						this.value = ((ValueDecoder)currentDecoder).getValue(context);
					}
					
					Decoder nextDecoder = currentDecoder.nextToBeEvaluated;

					// unlink to support garbage collection (avoid recursive traversing)
					currentDecoder.nextToBeEvaluated = null;
					currentDecoder.predecessor = null;
					
					currentDecoder = nextDecoder;
				}
			}
		} catch (Exception e) {
			throw new SAXException("error while traversing intermediate decoder nodes", e);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		topDecoder.appendCharacters(ch, start, length);
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		topDecoder.appendCharacters(ch, start, length);
	}

	@Override
	public void processingInstruction(String target, String rawData)
			throws SAXException {
		if (target.equals("gm-xml")) {
			Map<String, String> data = ProcessingInstructionParser.parseData(rawData);
			
			String encodedVersion = data.get("version");
			
			if (encodedVersion != null) {
				try {
					int version = Integer.parseInt(encodedVersion);
					context.setVersion(version);
				} catch (NumberFormatException e) {
					throw new SAXException("error while decoding gm-xml version", e);
				}
			}
		}

	}
}
