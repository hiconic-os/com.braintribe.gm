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

import java.util.Date;

import com.braintribe.model.generic.reflection.CloningContext;
import com.braintribe.model.generic.reflection.DateType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.tools.GmValueCodec;

public class DateTypeImpl extends AbstractSimpleType implements DateType {
	public static final DateTypeImpl INSTANCE = new DateTypeImpl();

	private DateTypeImpl() {
		super(java.util.Date.class);
	}

	@Override
	public String getTypeName() {
		return "date";
	}

	@Override
	public TypeCode getTypeCode() {
		return TypeCode.dateType;
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
	public <T> T instanceFromString(String encodedValue) throws GenericModelException {
		try {
			return (T) new Date(Long.parseLong(encodedValue));
		} catch (Exception e) {
			throw new GenericModelException("error while creating instance from string " + encodedValue, e);
		}
	}

	@Override
	public String instanceToString(Object value) throws GenericModelException {
		return Long.toString(((Date) value).getTime());
	}

	@Override
	public String instanceToGmString(Object value) throws GenericModelException {
		return GmValueCodec.dateToGmString((Date) value);
	}

	@Override
	public Object instanceFromGmString(String encodedValue) {
		return GmValueCodec.dateFromGmString(encodedValue);
	}

	@Override
	public Object cloneImpl(CloningContext cloningContext, Object value, StrategyOnCriterionMatch strategy) {
		return value == null ? null : new Date(((Date) value).getTime());
	}

	@Override
	public boolean isInstance(Object value) {
		return value instanceof java.util.Date;
	}

}
