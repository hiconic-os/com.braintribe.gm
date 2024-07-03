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
package com.braintribe.codec.marshaller.dom.coder.scalar;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.dom.DomDecodingContext;
import com.braintribe.codec.marshaller.dom.DomEncodingContext;
import com.braintribe.codec.marshaller.dom.TypeInfo;
import com.braintribe.codec.marshaller.dom.coder.DomTextCoder;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;

public class EnumDomCoder<T extends Enum<T>> extends DomTextCoder<T> {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	private EnumType enumType;
	
	
	public EnumDomCoder(EnumType enumType) {
		super("e");
		this.enumType = enumType;
	}
	
	public EnumDomCoder() {
		super("e");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T decodeText(DomDecodingContext context, String text) throws CodecException {
		int index = text.lastIndexOf('.');
		
		if (enumType == null) {
			String typeKey = text.substring(0, index); 
			enumType = (EnumType) context.getTypeInfoByKey(typeKey).type;
		}
		
		String constantName = text.substring(index + 1);
		return (T) enumType.getInstance(constantName);
	}
	
	@Override
	protected String encodeText(DomEncodingContext context, T value) throws CodecException {
		final EnumType type;
		if (enumType != null) {
			type = enumType;
		} else {
			type = typeReflection.getType(value);
		}

		final TypeInfo typeInfo = context.registerRequiredType(type);
		return typeInfo.as + "." + value;
	}
}
