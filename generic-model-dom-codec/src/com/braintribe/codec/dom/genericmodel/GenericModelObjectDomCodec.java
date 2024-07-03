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

public class GenericModelObjectDomCodec implements Codec<Object, Element> {
	private GenericModelDomCodecRegistry codecRegistry;
	
	public void setCodecRegistry(GenericModelDomCodecRegistry codecRegistry) {
		this.codecRegistry = codecRegistry;
	}
	
	@Override
	public Object decode(Element encodedValue) throws CodecException {
		String tagName = encodedValue.getTagName();
		Codec<?, Element> codec = codecRegistry.getCodecByTagName(tagName);
		return codec.decode(encodedValue);
	}
	
	@Override
	public Element encode(Object value) throws CodecException {
		GenericModelType actualType = GMF.getTypeReflection().getType(value);
		return codecRegistry.getCodec(actualType).encode(value);
	}
	
	@Override
	public Class<Object> getValueClass() {
		return Object.class;
	}
}
