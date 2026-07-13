package dev.hiconic.template.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.List;

import com.braintribe.gm.model.reason.ReasonException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.model.evaluation.NullPathElement;
import dev.hiconic.template.model.evaluation.PathEvaluationError;
import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.path.PropertyAccess;
import dev.hiconic.template.model.core.path.ListIndexAccess;
import dev.hiconic.template.model.core.path.MapKeyAccess;
import dev.hiconic.template.model.core.path.PathAccess;
import dev.hiconic.template.model.core.vd.TemplatePropertyPath;

public abstract class AbstractScopedTemplateEvaluationContext implements TemplateEvaluationContext {
	private static final class Binding {
		private Object value;
		private final boolean mutable;
		private Binding(Object value, boolean mutable) { this.value = value; this.mutable = mutable; }
	}

	private final Deque<Map<String, Binding>> variableScopes = new ArrayDeque<>();
	private final Deque<IdentityHashMap<Symbol, Binding>> symbolScopes = new ArrayDeque<>();

	protected AbstractScopedTemplateEvaluationContext() {
		variableScopes.push(new LinkedHashMap<>());
		symbolScopes.push(new IdentityHashMap<>());
	}

	@Override
	public void withVariables(Map<String, Object> variables, Runnable evaluation) {
		Map<String, Binding> scope = new LinkedHashMap<>();
		variables.forEach((name, value) -> scope.put(name, new Binding(value, false)));
		variableScopes.push(scope);
		symbolScopes.push(new IdentityHashMap<>());
		try {
			evaluation.run();
		} finally {
			variableScopes.pop();
			symbolScopes.pop();
		}
	}

	@Override
	public void withSymbolVariables(Map<Symbol, Object> variables, Runnable evaluation) {
		Map<String, Binding> names = new LinkedHashMap<>();
		IdentityHashMap<Symbol, Binding> identities = new IdentityHashMap<>();
		variables.forEach((symbol, value) -> {
			Binding binding = new Binding(value, false);
			names.put(symbol.getName(), binding);
			identities.put(symbol, binding);
		});
		variableScopes.push(names);
		symbolScopes.push(identities);
		try { evaluation.run(); } finally { variableScopes.pop(); symbolScopes.pop(); }
	}

	@Override
	public void declareVariable(String name, Object value) {
		declare(name, value, true);
	}

	@Override
	public void declareVariable(Symbol symbol, Object value) {
		declare(symbol.getName(), value, true);
		Binding binding = variableScopes.peek().get(symbol.getName());
		symbolScopes.peek().put(symbol, binding);
	}

	@Override
	public void declareReadonlyVariable(String name, Object value) {
		declare(name, value, false);
	}

	private void declare(String name, Object value, boolean mutable) {
		for (Map<String, Binding> scope : variableScopes)
			if (scope.containsKey(name))
				throw new IllegalArgumentException("Variable already visible and cannot be shadowed: " + name);
		variableScopes.peek().put(name, new Binding(value, mutable));
	}

	@Override
	public void setVariable(String name, Object value) {
		for (Map<String, Binding> scope : variableScopes)
			if (scope.containsKey(name)) {
				Binding binding = scope.get(name);
				if (!binding.mutable) throw new IllegalArgumentException("Variable is readonly: " + name);
				binding.value = value;
				return;
			}
		throw new IllegalArgumentException("Unknown variable: " + name);
	}

	@Override
	public void setVariable(Symbol symbol, Object value) {
		var symbolIterator = symbolScopes.iterator();
		var nameIterator = variableScopes.iterator();
		while (symbolIterator.hasNext() && nameIterator.hasNext()) {
			IdentityHashMap<Symbol, Binding> byIdentity = symbolIterator.next();
			Map<String, Binding> byName = nameIterator.next();
			Binding binding = byIdentity.get(symbol);
			if (binding == null) binding = byName.get(symbol.getName());
			if (binding != null) {
				if (!binding.mutable) throw new IllegalArgumentException("Variable is readonly: " + symbol.getName());
				binding.value = value;
				byIdentity.put(symbol, binding);
				return;
			}
		}
		throw new IllegalArgumentException("Unknown variable: " + symbol.getName());
	}

	@Override
	public Object getVariable(String name) {
		for (Map<String, Binding> scope : variableScopes)
			if (scope.containsKey(name))
				return scope.get(name).value;
		throw new IllegalArgumentException("Unknown variable: " + name);
	}

	@Override
	public Object getVariable(Symbol symbol) {
		var symbols = symbolScopes.iterator();
		var names = variableScopes.iterator();
		while (symbols.hasNext() && names.hasNext()) {
			IdentityHashMap<Symbol, Binding> byIdentity = symbols.next();
			Binding binding = byIdentity.get(symbol);
			if (binding != null) return binding.value;
			binding = names.next().get(symbol.getName());
			if (binding != null) {
				byIdentity.put(symbol, binding);
				return binding.value;
			}
		}
		throw new IllegalArgumentException("Unknown variable symbol: " + symbol.getName());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setProperty(TemplatePropertyPath path, Object value) {
		Object current = evaluate(path.getRoot());
		String fullPath = TemplatePaths.render(path);
		for (int i = 0; i < path.getAccesses().size(); i++) {
			PathAccess access = path.getAccesses().get(i);
			String segment = segment(access);
			if (current == null)
				throw new ReasonException(NullPathElement.create(fullPath, segment, "write", range(path, access)));
			boolean last = i + 1 == path.getAccesses().size();
			if (access instanceof PropertyAccess propertyAccess) {
				if (!(current instanceof GenericEntity entity))
					throw new ReasonException(PathEvaluationError.create(fullPath, segment, "write",
							"Receiver of '" + segment + "' in path '" + fullPath + "' is not an entity", range(path, access)));
				Property property = propertyAccess.getProperty().getResolvedProperty();
				if (property == null) {
					property = entity.entityType().findProperty(segment);
					if (property == null)
						throw new ReasonException(PathEvaluationError.create(fullPath, segment, "write",
								"Unknown property '" + segment + "' in path '" + fullPath + "'", range(path, access)));
					propertyAccess.getProperty().setResolvedProperty(property);
				}
				if (last) { property.set(entity, value); return; }
				current = property.get(entity);
			} else if (access instanceof ListIndexAccess indexAccess) {
				Object indexValue = evaluateAccessValue(indexAccess, ListIndexAccess.T.findProperty("index"));
				if (!(current instanceof List<?> list) || !(indexValue instanceof Integer index))
					throw accessError(path, access, fullPath, segment, "List access requires a list receiver and integer index");
				if (index < 0 || index >= list.size())
					throw accessError(path, access, fullPath, segment, "List index " + index + " is out of bounds");
				if (last) { ((List<Object>) list).set(index, value); return; }
				current = list.get(index);
			} else if (access instanceof MapKeyAccess keyAccess) {
				if (!(current instanceof Map<?, ?> map))
					throw accessError(path, access, fullPath, segment, "Map-key access requires a map receiver");
				Object key = evaluateAccessValue(keyAccess, MapKeyAccess.T.findProperty("key"));
				if (last) { ((Map<Object, Object>) map).put(key, value); return; }
				if (!map.containsKey(key))
					throw accessError(path, access, fullPath, segment, "Map key does not exist");
				current = map.get(key);
			} else {
				throw accessError(path, access, fullPath, segment, "Unsupported assignment access");
			}
			if (!last) current = TemplateValues.evaluate(this, current);
		}
	}

	private Object evaluateAccessValue(GenericEntity access, Property property) {
		com.braintribe.model.generic.value.ValueDescriptor descriptor = property.getVdDirect(access);
		return descriptor == null ? property.getDirect(access) : evaluate(descriptor);
	}

	private static ReasonException accessError(TemplatePropertyPath path, PathAccess access, String fullPath,
			String segment, String message) {
		return new ReasonException(PathEvaluationError.create(fullPath, segment, "write",
				message + " in path '" + fullPath + "'", range(path, access)));
	}

	private static String segment(PathAccess access) {
		return TemplatePaths.segment(access);
	}

	private static dev.hiconic.template.model.parse.TextRange range(TemplatePropertyPath path, PathAccess access) {
		return access.getSourceRange() == null ? path.getSourceRange() : access.getSourceRange();
	}

}
