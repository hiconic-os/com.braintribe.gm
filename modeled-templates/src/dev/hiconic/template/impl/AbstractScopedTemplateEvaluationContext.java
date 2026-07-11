package dev.hiconic.template.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.hiconic.template.api.TemplateEvaluationContext;

public abstract class AbstractScopedTemplateEvaluationContext implements TemplateEvaluationContext {
	private final Deque<Map<String, Object>> variableScopes = new ArrayDeque<>();

	protected AbstractScopedTemplateEvaluationContext() {
		variableScopes.push(new LinkedHashMap<>());
	}

	@Override
	public void withVariables(Map<String, Object> variables, Runnable evaluation) {
		variableScopes.push(new LinkedHashMap<>(variables));
		try {
			evaluation.run();
		} finally {
			variableScopes.pop();
		}
	}

	@Override
	public void declareVariable(String name, Object value) {
		if (variableScopes.peek().containsKey(name))
			throw new IllegalArgumentException("Variable already declared in current scope: " + name);
		variableScopes.peek().put(name, value);
	}

	@Override
	public void setVariable(String name, Object value) {
		for (Map<String, Object> scope : variableScopes)
			if (scope.containsKey(name)) {
				scope.put(name, value);
				return;
			}
		throw new IllegalArgumentException("Unknown variable: " + name);
	}

	@Override
	public Object getVariable(String name) {
		for (Map<String, Object> scope : variableScopes)
			if (scope.containsKey(name))
				return scope.get(name);
		throw new IllegalArgumentException("Unknown variable: " + name);
	}

}
