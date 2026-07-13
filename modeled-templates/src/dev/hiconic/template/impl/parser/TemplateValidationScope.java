package dev.hiconic.template.impl.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.reflection.GenericModelType;

public class TemplateValidationScope {
	public record Binding(GenericModelType type, boolean mutable) {}

	private final Deque<Map<String, Binding>> scopes = new ArrayDeque<>();

	public TemplateValidationScope(Map<String, GenericModelType> rootVariables) {
		Map<String, Binding> root = new LinkedHashMap<>();
		rootVariables.forEach((name, type) -> root.put(name, new Binding(type, false)));
		scopes.push(root);
	}

	public void enter() {
		scopes.push(new LinkedHashMap<>());
	}

	public void exit() {
		if (scopes.size() == 1)
			throw new IllegalStateException("Cannot exit the root template scope");
		scopes.pop();
	}

	public Maybe<GenericModelType> declare(String name, GenericModelType type) {
		return declare(name, type, false);
	}

	public Maybe<GenericModelType> declareMutable(String name, GenericModelType type) {
		return declare(name, type, true);
	}

	private Maybe<GenericModelType> declare(String name, GenericModelType type, boolean mutable) {
		if (find(name) != null)
			return Maybe.empty(ParseError.create("Variable already visible and cannot be shadowed: " + name));
		scopes.peek().put(name, new Binding(type, mutable));
		return Maybe.complete(type);
	}

	public Maybe<GenericModelType> resolve(String name) {
		Binding binding = find(name);
		if (binding != null) return Maybe.complete(binding.type());
		return Maybe.empty(ParseError.create("Unknown template variable: " + name));
	}

	public Maybe<Binding> resolveBinding(String name) {
		Binding binding = find(name);
		return binding == null
				? Maybe.empty(ParseError.create("Unknown template variable: " + name))
				: Maybe.complete(binding);
	}

	private Binding find(String name) {
		for (Map<String, Binding> scope : scopes) {
			Binding binding = scope.get(name);
			if (binding != null) return binding;
		}
		return null;
	}
}
