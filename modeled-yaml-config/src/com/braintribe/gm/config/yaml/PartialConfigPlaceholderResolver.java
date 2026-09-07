// ============================================================================
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
package com.braintribe.gm.config.yaml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.braintribe.gm.config.yaml.api.PartiallyResolvedConfiguration;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;

/**
 * Substitutes the available leaves of a placeholder expression while retaining expressions which still depend on unavailable variables.
 */
class PartialConfigPlaceholderResolver {
	private final ConfigVariableResolver variableResolver;
	private final Set<String> unresolvedVariables = new LinkedHashSet<>();
	private final IdentityHashMap<Object, Object> transformed = new IdentityHashMap<>();
	private Reason failure;

	PartialConfigPlaceholderResolver(ConfigVariableResolver variableResolver) {
		this.variableResolver = variableResolver;
	}

	<C> Maybe<PartiallyResolvedConfiguration<C>> resolve(C configuration) {
		Object result = transform(configuration, true);

		if (failure != null)
			return failure.asMaybe();

		@SuppressWarnings("unchecked")
		C typedResult = (C) result;
		return Maybe.complete(new PartiallyResolvedConfiguration<>(typedResult, unresolvedVariables));
	}

	private Object transform(Object value, boolean evaluateDescriptor) {
		if (value == null || failure != null)
			return value;

		if (VdHolder.isVdHolder(value))
			return transformDescriptor(((VdHolder) value).vd);

		if (value instanceof Variable variable)
			return resolveVariable(variable);

		if (value instanceof ValueDescriptor descriptor)
			return evaluateDescriptor ? transformDescriptor(descriptor) : transformEntity(descriptor, false);

		if (value instanceof GenericEntity entity)
			return transformEntity(entity, true);

		if (value instanceof Map<?, ?> map)
			return transformMap(map, evaluateDescriptor);

		if (value instanceof Collection<?> collection)
			return transformCollection(collection, evaluateDescriptor);

		return value;
	}

	private Object transformDescriptor(ValueDescriptor descriptor) {
		Object transformedDescriptor = transformEntity(descriptor, false);
		Set<String> descriptorUnresolved = collectUnresolvedVariables(transformedDescriptor);

		if (!descriptorUnresolved.isEmpty())
			return transformedDescriptor;

		Maybe<Object> evaluated = YamlConfigurations.resolvePlaceholders(transformedDescriptor,
				variable -> {
					throw new IllegalStateException("Unexpected unresolved variable " + variable.getName());
				});

		if (evaluated.isUnsatisfied()) {
			failure = evaluated.whyUnsatisfied();
			return transformedDescriptor;
		}

		return evaluated.get();
	}

	private Set<String> collectUnresolvedVariables(Object value) {
		Set<String> result = new LinkedHashSet<>();
		collectUnresolvedVariables(value, result, new IdentityHashMap<>());
		return result;
	}

	private void collectUnresolvedVariables(Object value, Set<String> result, IdentityHashMap<Object, Boolean> visited) {
		if (value == null || visited.put(value, Boolean.TRUE) != null)
			return;

		if (VdHolder.isVdHolder(value)) {
			collectUnresolvedVariables(((VdHolder) value).vd, result, visited);
			return;
		}

		if (value instanceof Variable variable) {
			result.add(variable.getName());
			return;
		}

		if (value instanceof GenericEntity entity) {
			for (Property property : entity.entityType().getProperties())
				collectUnresolvedVariables(property.getDirect(entity), result, visited);
			return;
		}

		if (value instanceof Map<?, ?> map) {
			map.forEach((key, entryValue) -> {
				collectUnresolvedVariables(key, result, visited);
				collectUnresolvedVariables(entryValue, result, visited);
			});
			return;
		}

		if (value instanceof Collection<?> collection)
			collection.forEach(element -> collectUnresolvedVariables(element, result, visited));
	}

	private Object resolveVariable(Variable variable) {
		Maybe<String> valueMaybe = variableResolver.resolveReasoned(variable);

		if (valueMaybe.isSatisfied())
			return valueMaybe.get();

		if (valueMaybe.isUnsatisfiedBy(NotFound.T)) {
			unresolvedVariables.add(variable.getName());
			return variable;
		}

		failure = valueMaybe.whyUnsatisfied();
		return variable;
	}

	private GenericEntity transformEntity(GenericEntity entity, boolean evaluateDescriptors) {
		if (transformed.put(entity, entity) != null)
			return entity;

		for (Property property : entity.entityType().getProperties()) {
			Object directValue = property.getDirect(entity);
			Object transformedValue = transform(directValue, evaluateDescriptors);

			if (transformedValue == directValue)
				continue;

			if (transformedValue instanceof ValueDescriptor descriptor)
				property.setVdDirect(entity, descriptor);
			else
				property.setDirectUnsafe(entity, transformedValue);
		}

		return entity;
	}

	private Map<Object, Object> transformMap(Map<?, ?> map, boolean evaluateDescriptors) {
		Object known = transformed.get(map);
		if (known != null)
			return castMap(known);

		Map<Object, Object> result = new LinkedHashMap<>();
		transformed.put(map, result);
		map.forEach((key, value) -> result.put(transform(key, evaluateDescriptors), transform(value, evaluateDescriptors)));
		return result;
	}

	private Collection<Object> transformCollection(Collection<?> collection, boolean evaluateDescriptors) {
		Object known = transformed.get(collection);
		if (known != null)
			return castCollection(known);

		Collection<Object> result = collection instanceof Set<?> ? new LinkedHashSet<>() : new ArrayList<>(collection.size());
		transformed.put(collection, result);
		for (Object element : collection)
			result.add(transform(element, evaluateDescriptors));
		return result;
	}

	@SuppressWarnings("unchecked")
	private static Map<Object, Object> castMap(Object value) {
		return (Map<Object, Object>) value;
	}

	@SuppressWarnings("unchecked")
	private static Collection<Object> castCollection(Object value) {
		return (Collection<Object>) value;
	}
}
