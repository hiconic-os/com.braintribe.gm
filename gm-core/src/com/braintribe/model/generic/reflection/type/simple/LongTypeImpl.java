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

import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.LongType;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.tools.GmValueCodec;

public class LongTypeImpl extends AbstractSimpleType implements LongType {
	public static final LongTypeImpl INSTANCE = new LongTypeImpl();

	private LongTypeImpl() {
		super(Long.class);
	}

	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public String getTypeName() {
		return "long";
	}

	@Override
	public TypeCode getTypeCode() {
		return TypeCode.longType;
	}

	@Override
	public Object getDefaultValue() {
		return 0L;
	}

	@Override
	public Class<?> getPrimitiveJavaType() {
		return long.class;
	}

	@Override
	public <T> T instanceFromString(String s) throws GenericModelException {
		return (T) Long.valueOf(s);
	}

	@Override
	public String instanceToGmString(Object value) throws GenericModelException {
		return GmValueCodec.longToGmString((Long)value);
	}

	@Override
	public Object instanceFromGmString(String encodedValue) {
		return GmValueCodec.longFromGmString(encodedValue);
	}

}
