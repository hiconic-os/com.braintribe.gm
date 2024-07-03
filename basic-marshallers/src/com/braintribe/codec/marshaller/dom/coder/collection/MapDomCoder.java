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
package com.braintribe.codec.marshaller.dom.coder.collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.dom.DomDecodingContext;
import com.braintribe.codec.marshaller.dom.DomEncodingContext;
import com.braintribe.codec.marshaller.dom.coder.DeferredDecoder;
import com.braintribe.codec.marshaller.dom.coder.DeferredEncoder;
import com.braintribe.codec.marshaller.dom.coder.DomCoder;

public class MapDomCoder<K, V> implements DomCoder<Map<K, V>> {
	private DomCoder<K> keyCoder;
	private DomCoder<V> valueCoder;
	private boolean returnNullOnEmptyCollection;

	public MapDomCoder(DomCoder<K> keyCoder, DomCoder<V> valueCoder) {
		super();
		this.keyCoder = keyCoder;
		this.valueCoder = valueCoder;
	}
	
	public MapDomCoder(DomCoder<K> keyCoder, DomCoder<V> valueCoder, boolean returnNullOnEmptyCollection) {
		super();
		this.keyCoder = keyCoder;
		this.valueCoder = valueCoder;
		this.returnNullOnEmptyCollection = returnNullOnEmptyCollection;
	}

	@Override
	public Map<K, V> decode(DomDecodingContext context, final Element element) throws CodecException {
		if (element.getTagName().equals("n"))
			return null;
		
		final Map<K, V> map = new HashMap<K, V>();
		final int maxDeferred = context.getMaxDeferred();

		if (maxDeferred != -1) {
			context.appendDeferredDecoder(new DeferredDecoder() {
				private Node node = element.getFirstChild();
				@Override
				public boolean continueDecode(DomDecodingContext context) throws CodecException {
					node = decode(context, map, node, maxDeferred);
					return node != null;
				}
			});
		}
		else {
			Node node = element.getFirstChild();
			decode(context, map, node, -1);
		}

		return map;
	}
	
	@Override
	public Element encode(DomEncodingContext context, Map<K, V> value) throws CodecException {
		final Document document = context.getDocument();
		if (value == null)
			return document.createElement("n");
		
		if (returnNullOnEmptyCollection && value.isEmpty())
			return null;
		
		final Element element = document.createElement("M");
		final int maxDeferred = context.getMaxDeferred();
		final Iterator<Map.Entry<K, V>> it = value.entrySet().iterator();
		
		if (maxDeferred != -1) {
			context.appendDeferredEncoder(new DeferredEncoder() {
				@Override
				public boolean continueEncode(DomEncodingContext context) throws CodecException {
					return encode(context, it, element, maxDeferred);
				}
			});
		}
		else {
			encode(context, it, element, -1);
		}

		return element;
	}
	
	
	private Node decode(DomDecodingContext context, Map<K, V> map, Node node, int maxDecode) throws CodecException {
		int i = 0;
		while (node != null) {
			
			if (maxDecode != -1 && i == maxDecode) {
				return node;
			}
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element entryElement = (Element)node;
				
				Element []childElements = new Element[2];
				
				Node childNode = entryElement.getFirstChild();
				
				int n = 0;
				while (childNode != null) {
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						childElements[n++] = (Element)childNode;
						if (n == 2)
							break;
					}
					childNode = childNode.getNextSibling();
				}
				
				if (n != 2)
					throw new CodecException("map entry element is missing child elements");
				
				K key = keyCoder.decode(context, childElements[0]);
				V value = valueCoder.decode(context, childElements[1]);
				
				map.put(key, value);
				i++;
			}
			
			node = node.getNextSibling();
		}
		
		return null;
	}
	
	private boolean encode(DomEncodingContext context, Iterator<Map.Entry<K, V>> it, Element element, int maxEncode) throws CodecException {
		int i = 0;
		Document document = context.getDocument();
		while (it.hasNext()) {
			
			if (maxEncode != -1 && i >= maxEncode) 
				return true;
			
			Map.Entry<K, V> entry = it.next();

			Element entryElement = document.createElement("m");
			Element keyElement = keyCoder.encode(context, entry.getKey());
			Element valueElement = valueCoder.encode(context, entry.getValue());
			entryElement.appendChild(keyElement);
			entryElement.appendChild(valueElement);
			element.appendChild(entryElement);
			
			i += 2;
		}
		
		return false;
	}

}
