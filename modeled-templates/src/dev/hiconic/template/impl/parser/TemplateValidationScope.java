package dev.hiconic.template.impl.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.reflection.GenericModelType;

public class TemplateValidationScope {
	private final Deque<Map<String, GenericModelType>> scopes = new ArrayDeque<>();

	public TemplateValidationScope(Map<String, GenericModelType> rootVariables) {
		scopes.push(new LinkedHashMap<>(rootVariables));
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
		if (scopes.peek().putIfAbsent(name, type) != null)
			return Maybe.empty(ParseError.create("Variable already declared in current scope: " + name));
		return Maybe.complete(type);
	}

	public Maybe<GenericModelType> resolve(String name) {
		for (Map<String, GenericModelType> scope : scopes) {
			GenericModelType type = scope.get(name);
			if (type != null)
				return Maybe.complete(type);
		}
		return Maybe.empty(ParseError.create("Unknown template variable: " + name));
	}
}
