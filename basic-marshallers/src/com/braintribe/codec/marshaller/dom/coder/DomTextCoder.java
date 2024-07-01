// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.codec.marshaller.dom.coder;

import org.w3c.dom.Element;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.dom.DomDecodingContext;
import com.braintribe.codec.marshaller.dom.DomEncodingContext;

public abstract class DomTextCoder<T> implements DomCoder<T> {
	private String elementName;
	
	public DomTextCoder(String elementName) {
		super();
		this.elementName = elementName;
	}

	@Override
	public T decode(DomDecodingContext context, Element element) throws CodecException {
		if (element.getTagName().equals("n"))
			return null;
		
		String text = element.getTextContent();
		return decodeText(context, text);
	}

	protected abstract T decodeText(DomDecodingContext context, String text) throws CodecException;

	@Override
	public Element encode(DomEncodingContext context, T value) throws CodecException {
		if (value == null)
			context.getDocument().createElement("n");
		
		String text = encodeText(context, value);
		
		Element element = context.getDocument().createElement(elementName);
		
		if (text != null)
			element.setTextContent(text);
		
		return element;
	}

	/**
	 * @param context
	 * @throws CodecException
	 */
	protected String encodeText(DomEncodingContext context, T value) throws CodecException {
		return value.toString();
	}
	
}
