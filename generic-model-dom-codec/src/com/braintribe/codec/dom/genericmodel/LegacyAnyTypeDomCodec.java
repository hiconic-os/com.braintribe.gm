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
package com.braintribe.codec.dom.genericmodel;

import org.w3c.dom.Element;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.utils.xml.XmlTools;

public class LegacyAnyTypeDomCodec implements Codec<Object, Element> {
	private GenericModelDomCodecRegistry codecRegistry;
	private final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();
	
	public void setCodecRegistry(GenericModelDomCodecRegistry codecRegistry) {
		this.codecRegistry = codecRegistry;
	}
	
	@Override
	public Element encode(Object value) throws CodecException {
		throw new UnsupportedOperationException("Any Types are not supported. Just decoding of older encoded xmls could still be read.");
	}
	
	@Override
	public Object decode(Element encodedValue) throws CodecException {
		String typeSignature = encodedValue.getAttribute("type");
		
		if (typeSignature == null || typeSignature.length() == 0)
			throw new CodecException("AnyType must have a type attribute to be decoded from a DOM Element");
		
		Element childElement = XmlTools.getFirstElement(encodedValue, null);
		GenericModelType type = typeReflection.getType(typeSignature);
		Codec<Object, Element> codec = codecRegistry.getCodec(type);
		
		Object value = codec.decode(childElement);
		return value;
	}

	@Override
	public Class<Object> getValueClass() {
		return Object.class;
	}
}
