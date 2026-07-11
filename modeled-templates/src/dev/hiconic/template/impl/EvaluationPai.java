package dev.hiconic.template.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;

public class EvaluationPai extends PropertyAccessInterceptor {
	
	@Override
	public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
		Object value = property.getDirectUnsafe(entity);

		ValueDescriptor vd = descriptor(value);

		if (!TemplateEvaluationContext.CURRENT.isBound())
			return vd == null ? value : null;

		TemplateEvaluationContext context = TemplateEvaluationContext.CURRENT.get();

		if (vd != null)
			return context.evaluate(vd);

		if (value instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) value;
			return evaluateMap(context, map);
		}

		if (value instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>) value;
			return evaluateCollection(context, collection);
		}

		return value;
	}

	private static Object evaluateCollection(TemplateEvaluationContext context, Collection<?> source) {
		Collection<Object> result = null;
		int index = 0;

		for (Object element : source) {
			ValueDescriptor vd = descriptor(element);

			if (vd != null) {
				if (result == null) {
					result = newCollectionLike(source);
					copyPrefix(source, result, index);
				}
				result.add(context.evaluate(vd));
			} else if (result != null) {
				result.add(element);
			}

			index++;
		}

		return result == null ? source : result;
	}

	private static Object evaluateMap(TemplateEvaluationContext context, Map<?, ?> source) {
		Map<Object, Object> result = null;
		int index = 0;

		for (Map.Entry<?, ?> entry : source.entrySet()) {
			ValueDescriptor keyDescriptor = descriptor(entry.getKey());
			ValueDescriptor valueDescriptor = descriptor(entry.getValue());

			if (keyDescriptor != null || valueDescriptor != null) {
				if (result == null) {
					result = new LinkedHashMap<>(source.size());
					copyPrefix(source, result, index);
				}

				Object key = keyDescriptor == null ? entry.getKey() : context.evaluate(keyDescriptor);
				Object value = valueDescriptor == null ? entry.getValue() : context.evaluate(valueDescriptor);
				result.put(key, value);
			} else if (result != null) {
				result.put(entry.getKey(), entry.getValue());
			}

			index++;
		}

		return result == null ? source : result;
	}

	private static Collection<Object> newCollectionLike(Collection<?> source) {
		if (source instanceof Set<?>)
			return new LinkedHashSet<>(capacity(source.size()));

		return new ArrayList<>(source.size());
	}

	private static void copyPrefix(Collection<?> source, Collection<Object> target, int prefixSize) {
		int index = 0;
		for (Object element : source) {
			if (index++ == prefixSize)
				break;
			target.add(element);
		}
	}

	private static void copyPrefix(Map<?, ?> source, Map<Object, Object> target, int prefixSize) {
		int index = 0;
		for (Map.Entry<?, ?> entry : source.entrySet()) {
			if (index++ == prefixSize)
				break;
			target.put(entry.getKey(), entry.getValue());
		}
	}

	private static int capacity(int size) {
		if (size < 3)
			return size + 1;
		if (size < 1 << 30)
			return (int) (size / .75f + 1);
		return Integer.MAX_VALUE;
	}

	private static ValueDescriptor descriptor(Object value) {
		if (value instanceof ValueDescriptor descriptor)
			return descriptor;
		return VdHolder.getValueDescriptorIfPossible(value);
	}
}
