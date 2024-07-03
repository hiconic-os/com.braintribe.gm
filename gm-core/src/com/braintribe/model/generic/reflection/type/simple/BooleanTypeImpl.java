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
package com.braintribe.model.generic.reflection.type.simple;

import com.braintribe.model.generic.reflection.BooleanType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.tools.GmValueCodec;

public class BooleanTypeImpl extends AbstractSimpleType implements BooleanType {
	public static final BooleanTypeImpl INSTANCE = new BooleanTypeImpl();

	private BooleanTypeImpl() {
		super(Boolean.class);
	}

	@Override
	public String getTypeName() {
		return "boolean";
	}

	@Override
	public TypeCode getTypeCode() {
		return TypeCode.booleanType;
	}

	@Override
	public Object getDefaultValue() {
		return Boolean.FALSE;
	}

	@Override
	public Class<?> getPrimitiveJavaType() {
		return boolean.class;
	}

	@Override
	public <T> T instanceFromString(String encodedValue) throws GenericModelException {
		return (T) Boolean.valueOf(encodedValue);
	}

	@Override
	public String instanceToGmString(Object value) throws GenericModelException {
		return GmValueCodec.booleanToGmString((Boolean) value);
	}

	@Override
	public Object instanceFromGmString(String encodedValue) {
		return GmValueCodec.booleanFromGmString(encodedValue);
	}

}
