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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.convert;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.braintribe.model.bvd.convert.ToReference;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

public class ToReferenceVde implements ValueDescriptorEvaluator<ToReference> {

	@Override
	public VdeResult evaluate(VdeContext context, ToReference valueDescriptor) throws VdeRuntimeException {
		Object operand = context.evaluate(valueDescriptor.getOperand());
		Object result = convert(operand);
		return new VdeResultImpl(result, false);
	}

	private Object convert(Object operand) {
		if (operand == null) {
			return null;
		}
		GenericModelType operandType = BaseType.INSTANCE.getActualType(operand);
		TypeCode typeCode = operandType.getTypeCode();
		switch (typeCode) {
			case entityType:
				return ((GenericEntity) operand).reference();
			case enumType:
				return EnumReference.of((Enum<?>)operand);
			case listType:
			case setType:
				Collection<?> collection = (Collection<?>) operand;
				//@formatter:off
				return collection
					.stream()
					.map(this::convert)
					.collect(getCollector(typeCode));
				//@formatter:on
			case mapType:
				Map<?,?> map = (Map<?,?>) operand;
				//@formatter:off
				return map.entrySet()
					.stream()
					.collect(Collectors.toMap(this::convert, this::convert));
				//@formatter:on
			default:
				return operand;
		}
	}

	private Collector<Object, ?, ? extends Collection<Object>> getCollector(TypeCode typeCode) {
		return (typeCode == TypeCode.setType) ? Collectors.toSet() : Collectors.toList();
	}

}
