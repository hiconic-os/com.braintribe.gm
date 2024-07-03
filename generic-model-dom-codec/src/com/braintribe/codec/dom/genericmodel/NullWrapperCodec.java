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

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;

public class NullWrapperCodec<T> implements Codec<T, Element> {
	private Codec<T, Element> valueCodec;
	@Configurable @Required
	public void setValueCodec(Codec<T, Element> valueCodec) {
		this.valueCodec = valueCodec;
	}
	
	@Override
	public T decode(Element element) throws CodecException {
		if (element.getTagName().equals("null"))
			return null;
		
		return valueCodec.decode(element);
	}
	
	@Override
	public Element encode(T value) throws CodecException {
		EncodingContext ctx = EncodingContext.get();
		if (value == null) {
			return ctx.getDocument().createElement("null");
		}
		else {
			return valueCodec.encode(value);
		}
	}
	
	@Override
	public Class<T> getValueClass() {
		return valueCodec.getValueClass();
	}
}
