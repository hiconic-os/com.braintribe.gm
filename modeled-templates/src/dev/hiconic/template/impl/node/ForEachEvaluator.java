package dev.hiconic.template.impl.node;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.instr.ForEach;

public class ForEachEvaluator implements TemplateNodeEvaluator<ForEach> {
	@Override
	public void evaluate(TemplateEvaluationContext context, ForEach node) {
		Iterator<?> iterator = iterator(node.getIterable());
		if (!iterator.hasNext()) {
			if (node.getEmpty() != null)
				context.evaluate(node.getEmpty());
			return;
		}

		int index = 0;
		while (iterator.hasNext()) {
			Map<String, Object> variables = new LinkedHashMap<>(2);
			variables.put(node.getVariable(), iterator.next());
			if (node.getIndexVariable() != null)
				variables.put(node.getIndexVariable(), index);
			context.withVariables(variables, () -> context.evaluate(node.getBlock()));
			index++;
		}
	}

	@Override
	public Reason validate(ValidationContext context, ForEach node) {
		if (node.getVariable() == null || node.getVariable().isBlank())
			return InvalidArgument.create("ForEach.variable must not be blank");
		if (node.getBlock() == null)
			return InvalidArgument.mandatoryPropertyNull(ForEach.T, ForEach.block.name());

		GenericModelType type = context.getType(node, ForEach.iterable);
		if (type == null || !type.isCollection() && !type.isBase())
			return InvalidArgument.create("ForEach.iterable must evaluate to a collection, but evaluates to "
					+ (type == null ? "<unknown>" : type.getTypeSignature()));

		return null;
	}

	private static Iterator<?> iterator(Object value) {
		if (value instanceof Map<?, ?> map)
			return map.entrySet().iterator();
		if (value instanceof Iterable<?> iterable)
			return iterable.iterator();
		if (value != null && value.getClass().isArray())
			return new Iterator<>() {
				private final int length = Array.getLength(value);
				private int index;

				@Override
				public boolean hasNext() {
					return index < length;
				}

				@Override
				public Object next() {
					return Array.get(value, index++);
				}
			};
		throw new IllegalArgumentException("ForEach.iterable is not iterable: "
				+ (value == null ? "null" : value.getClass().getName()));
	}
}
