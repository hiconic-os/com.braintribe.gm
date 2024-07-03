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

import java.math.BigDecimal;

import com.braintribe.model.generic.reflection.DecimalType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.tools.GmValueCodec;

public class DecimalTypeImpl extends AbstractSimpleType implements DecimalType {
	public static final DecimalTypeImpl INSTANCE = new DecimalTypeImpl();

	private DecimalTypeImpl() {
		super(BigDecimal.class);
	}

	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public String getTypeName() {
		return "decimal";
	}

	@Override
	public TypeCode getTypeCode() {
		return TypeCode.decimalType;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public Class<?> getPrimitiveJavaType() {
		return null;
	}

	@Override
	public <T> T instanceFromString(String s) throws GenericModelException {
		if (s.length() == 1) {
			return (T) BigDecimal.valueOf(s.charAt(0) - '0');
		}

		if ("10".equals(s)) {
			return (T) BigDecimal.TEN;
		}

		return (T) new BigDecimal(s);
	}

	@Override
	public String instanceToGmString(Object value) throws GenericModelException {
		return GmValueCodec.decimalToGmString((BigDecimal) value);
	}

	@Override
	public Object instanceFromGmString(String encodedValue) {
		return GmValueCodec.decimalFromGmString(encodedValue);
	}

}
