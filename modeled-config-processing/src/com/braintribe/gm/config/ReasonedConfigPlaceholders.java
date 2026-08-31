// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.gm.config;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.bvd.convert.ToBoolean;
import com.braintribe.model.bvd.convert.ToDate;
import com.braintribe.model.bvd.convert.ToDecimal;
import com.braintribe.model.bvd.convert.ToDouble;
import com.braintribe.model.bvd.convert.ToFloat;
import com.braintribe.model.bvd.convert.ToInteger;
import com.braintribe.model.bvd.convert.ToLong;
import com.braintribe.model.bvd.convert.ToString;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.vde.reasoned.api.ResidualValuePolicy;
import com.braintribe.model.processing.vde.reasoned.impl.ReasonedValueDescriptorMaterializer;
import com.braintribe.model.processing.vde.reasoned.impl.StandardValueDescriptorEvaluationContext;
import com.braintribe.model.processing.vde.reasoned.impl.ValueDescriptorExpertRegistry;
import com.braintribe.utils.format.lcd.FormatTool;

/** Reasoned evaluation setup for the value descriptors emitted by modeled-configuration placeholder parsing. */
public final class ReasonedConfigPlaceholders {

	private ReasonedConfigPlaceholders() {
	}

	public static <E> Maybe<E> resolve(E config, Function<Variable, Maybe<?>> variableResolver) {
		return resolve(config, variableResolver, ResidualValuePolicy.rejectAll());
	}

	public static <E> Maybe<E> resolve(E config, Function<Variable, Maybe<?>> variableResolver,
			ResidualValuePolicy residualValuePolicy) {
		return resolve(config, variableResolver, residualValuePolicy, registry -> {}, context -> {});
	}

	/** Extensible setup for configuration domains contributing typed descriptors and evaluation aspects. */
	public static <E> Maybe<E> resolve(E config, Function<Variable, Maybe<?>> variableResolver,
			ResidualValuePolicy residualValuePolicy, Consumer<ValueDescriptorExpertRegistry> registryConfigurer,
			Consumer<StandardValueDescriptorEvaluationContext> contextConfigurer) {
		ValueDescriptorExpertRegistry registry = registry(variableResolver);
		registryConfigurer.accept(registry);
		StandardValueDescriptorEvaluationContext context = new StandardValueDescriptorEvaluationContext(registry);
		contextConfigurer.accept(context);
		return new ReasonedValueDescriptorMaterializer(context, residualValuePolicy).materialize(config);
	}

	public static ValueDescriptorExpertRegistry registry(Function<Variable, Maybe<?>> variableResolver) {
		ValueDescriptorExpertRegistry registry = new ValueDescriptorExpertRegistry();

		registry.register(Variable.T, (context, descriptor) -> variableResolver.apply(descriptor));
		registry.register(Concatenation.T, (context, descriptor) -> concatenate(descriptor.getOperands()));
		registry.register(ToString.T, (context, descriptor) -> toString(descriptor.getOperand(), descriptor.getFormat()));
		registry.register(ToBoolean.T, (context, descriptor) -> toBoolean(descriptor.getOperand(), descriptor.getFormat()));
		registry.register(ToInteger.T, (context, descriptor) -> toInteger(descriptor.getOperand(), descriptor.getFormat()));
		registry.register(ToLong.T, (context, descriptor) -> toLong(descriptor.getOperand(), descriptor.getFormat()));
		registry.register(ToFloat.T, (context, descriptor) -> toFloat(descriptor.getOperand(), descriptor.getFormat()));
		registry.register(ToDouble.T, (context, descriptor) -> toDouble(descriptor.getOperand(), descriptor.getFormat()));
		registry.register(ToDecimal.T, (context, descriptor) -> toDecimal(descriptor.getOperand(), descriptor.getFormat()));
		registry.register(ToDate.T, (context, descriptor) -> toDate(descriptor.getOperand(), descriptor.getFormat()));

		return registry;
	}

	private static Maybe<String> concatenate(List<Object> operands) {
		if (operands == null)
			return invalid("Null operands are not allowed for Concatenation");

		StringBuilder result = new StringBuilder();
		for (Object operand : operands) {
			if (!(operand instanceof String))
				return invalid("Unable to concatenate non-string operand: " + operand);
			result.append((String) operand);
		}
		return Maybe.complete(result.toString());
	}

	private static Maybe<String> toString(Object operand, Object format) {
		if (operand == null)
			return invalid("Convert ToString is not applicable to null");

		try {
			if (operand instanceof Date) {
				if (format == null)
					return Maybe.complete(operand.toString());
				if (!(format instanceof String))
					return invalid("Convert ToString with Date operand requires a string format");
				return Maybe.complete(FormatTool.getExpert().getDateFormat().formatDate((Date) operand, (String) format));
			}

			if (operand instanceof Number && format != null) {
				if (!(format instanceof String))
					return invalid("Convert ToString with Number operand requires a string format");
				return Maybe.complete(FormatTool.getExpert().getNumberFormat().format((Number) operand, (String) format));
			}

			return Maybe.complete(operand.toString());
		} catch (RuntimeException e) {
			return invalid("Unable to convert value to String: " + e.getMessage());
		}
	}

	private static Maybe<Boolean> toBoolean(Object operand, Object format) {
		if (format != null)
			return invalid("Format is not supported for ToBoolean: " + format);
		if (!(operand instanceof String))
			return invalid("Convert ToBoolean is not applicable to: " + operand);
		return Maybe.complete(Boolean.parseBoolean((String) operand));
	}

	private static Maybe<Integer> toInteger(Object operand, Object format) {
		if (format != null)
			return invalid("Format is not supported for ToInteger: " + format);
		try {
			if (operand instanceof String)
				return Maybe.complete(Integer.parseInt((String) operand));
			if (operand instanceof Boolean)
				return Maybe.complete((Boolean) operand ? 1 : 0);
			if (operand instanceof Enum<?>)
				return Maybe.complete(((Enum<?>) operand).ordinal());
			return invalid("Convert ToInteger is not applicable to: " + operand);
		} catch (RuntimeException e) {
			return invalid("Unable to convert value to Integer: " + e.getMessage());
		}
	}

	private static Maybe<Long> toLong(Object operand, Object format) {
		if (format != null)
			return invalid("Format is not supported for ToLong: " + format);
		try {
			if (operand instanceof String)
				return Maybe.complete(Long.parseLong((String) operand));
			if (operand instanceof Boolean)
				return Maybe.complete((Boolean) operand ? 1L : 0L);
			if (operand instanceof Date)
				return Maybe.complete(((Date) operand).getTime());
			return invalid("Convert ToLong is not applicable to: " + operand);
		} catch (RuntimeException e) {
			return invalid("Unable to convert value to Long: " + e.getMessage());
		}
	}

	private static Maybe<Float> toFloat(Object operand, Object format) {
		if (format != null)
			return invalid("Format is not supported for ToFloat: " + format);
		try {
			if (operand instanceof String)
				return Maybe.complete(Float.parseFloat((String) operand));
			if (operand instanceof Boolean)
				return Maybe.complete((Boolean) operand ? 1F : 0F);
			return invalid("Convert ToFloat is not applicable to: " + operand);
		} catch (RuntimeException e) {
			return invalid("Unable to convert value to Float: " + e.getMessage());
		}
	}

	private static Maybe<Double> toDouble(Object operand, Object format) {
		if (format != null)
			return invalid("Format is not supported for ToDouble: " + format);
		try {
			if (operand instanceof String)
				return Maybe.complete(Double.parseDouble((String) operand));
			if (operand instanceof Boolean)
				return Maybe.complete((Boolean) operand ? 1D : 0D);
			return invalid("Convert ToDouble is not applicable to: " + operand);
		} catch (RuntimeException e) {
			return invalid("Unable to convert value to Double: " + e.getMessage());
		}
	}

	private static Maybe<BigDecimal> toDecimal(Object operand, Object format) {
		try {
			if (operand instanceof String) {
				if (format == null)
					return Maybe.complete(new BigDecimal((String) operand));
				if (!(format instanceof String))
					return invalid("Convert ToDecimal with String operand requires a string format");
				return Maybe.complete(new BigDecimal((String) operand, new MathContext((String) format)));
			}
			if (operand instanceof Boolean && format == null)
				return Maybe.complete((Boolean) operand ? BigDecimal.ONE : BigDecimal.ZERO);
			return invalid("Convert ToDecimal is not applicable to: " + operand + " with format: " + format);
		} catch (RuntimeException e) {
			return invalid("Unable to convert value to Decimal: " + e.getMessage());
		}
	}

	private static Maybe<Date> toDate(Object operand, Object format) {
		try {
			if (operand instanceof String) {
				if (!(format instanceof String))
					return invalid("Convert ToDate with String operand requires a string format");
				return Maybe.complete(FormatTool.getExpert().getDateFormat().parseDate((String) operand, (String) format));
			}
			if (operand instanceof Long && format == null)
				return Maybe.complete(new Date((Long) operand));
			return invalid("Convert ToDate is not applicable to: " + operand + " with format: " + format);
		} catch (RuntimeException e) {
			return invalid("Unable to convert value to Date: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> Maybe<T> invalid(String message) {
		return (Maybe<T>) InvalidArgument.create(message).asMaybe();
	}
}
