package dev.hiconic.template.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.braintribe.model.generic.collection.ListBase;
import com.braintribe.model.generic.collection.MapBase;
import com.braintribe.model.generic.collection.PlainList;
import com.braintribe.model.generic.collection.PlainMap;
import com.braintribe.model.generic.collection.PlainSet;
import com.braintribe.model.generic.collection.SetBase;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;

/** Materializes VD-backed values recursively while preserving modeled collection types. */
public final class TemplateValues {
	private TemplateValues() {}

	public static Object evaluate(TemplateEvaluationContext context, Object value) {
		ValueDescriptor descriptor = descriptor(value);
		if (descriptor != null) {
			Object evaluated = context.evaluate(descriptor);
			return evaluated == value || evaluated == descriptor ? evaluated : evaluate(context, evaluated);
		}
		if (value instanceof Map<?, ?> map) return evaluateMap(context, map);
		if (value instanceof Collection<?> collection) return evaluateCollection(context, collection);
		return value;
	}

	private static Object evaluateCollection(TemplateEvaluationContext context, Collection<?> source) {
		Collection<Object> result = null;
		int index = 0;
		for (Object element : source) {
			Object evaluated = evaluate(context, element);
			if (result == null && evaluated != element) {
				result = newCollectionLike(source);
				copyPrefix(source, result, index);
			}
			if (result != null) result.add(evaluated);
			index++;
		}
		return result == null ? source : result;
	}

	private static Object evaluateMap(TemplateEvaluationContext context, Map<?, ?> source) {
		Map<Object, Object> result = null;
		int index = 0;
		for (Map.Entry<?, ?> entry : source.entrySet()) {
			Object key = evaluate(context, entry.getKey());
			Object value = evaluate(context, entry.getValue());
			if (result == null && (key != entry.getKey() || value != entry.getValue())) {
				result = newMapLike(source);
				copyPrefix(source, result, index);
			}
			if (result != null) result.put(key, value);
			index++;
		}
		return result == null ? source : result;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Collection<Object> newCollectionLike(Collection<?> source) {
		if (source instanceof ListBase<?> list) return new PlainList(list.type());
		if (source instanceof SetBase<?> set) return new PlainSet(set.type());
		if (source instanceof java.util.Set<?>) return new LinkedHashSet<>(capacity(source.size()));
		return new ArrayList<>(source.size());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Map<Object, Object> newMapLike(Map<?, ?> source) {
		return source instanceof MapBase<?, ?> map
				? new PlainMap(map.type()) : new LinkedHashMap<>(capacity(source.size()));
	}

	private static void copyPrefix(Collection<?> source, Collection<Object> target, int prefixSize) {
		int index = 0;
		for (Object element : source) {
			if (index++ == prefixSize) break;
			target.add(element);
		}
	}

	private static void copyPrefix(Map<?, ?> source, Map<Object, Object> target, int prefixSize) {
		int index = 0;
		for (Map.Entry<?, ?> entry : source.entrySet()) {
			if (index++ == prefixSize) break;
			target.put(entry.getKey(), entry.getValue());
		}
	}

	private static int capacity(int size) {
		if (size < 3) return size + 1;
		if (size < 1 << 30) return (int) (size / .75f + 1);
		return Integer.MAX_VALUE;
	}

	private static ValueDescriptor descriptor(Object value) {
		return value instanceof ValueDescriptor descriptor
				? descriptor : VdHolder.getValueDescriptorIfPossible(value);
	}
}
