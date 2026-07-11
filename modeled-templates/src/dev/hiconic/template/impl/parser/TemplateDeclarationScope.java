package dev.hiconic.template.impl.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;

import dev.hiconic.template.model.core.decl.DeclareInstruction;

public class TemplateDeclarationScope {
	private final Deque<Map<String, DeclareInstruction>> scopes = new ArrayDeque<>();

	public TemplateDeclarationScope() {
		scopes.push(new LinkedHashMap<>());
	}

	public void enter() {
		scopes.push(new LinkedHashMap<>());
	}

	public void exit() {
		if (scopes.size() == 1)
			throw new IllegalStateException("Cannot exit root declaration scope");
		scopes.pop();
	}

	public Maybe<DeclareInstruction> declare(DeclareInstruction declaration) {
		if (declaration.getName() == null || declaration.getName().isBlank())
			return Maybe.empty(ParseError.create("Instruction declaration name must not be blank"));
		if (scopes.peek().putIfAbsent(declaration.getName(), declaration) != null)
			return Maybe.empty(ParseError.create(
					"Instruction already declared in current scope: " + declaration.getName()));
		return Maybe.complete(declaration);
	}

	public Maybe<DeclareInstruction> resolve(String name) {
		for (Map<String, DeclareInstruction> scope : scopes) {
			DeclareInstruction declaration = scope.get(name);
			if (declaration != null)
				return Maybe.complete(declaration);
		}
		return Maybe.empty(ParseError.create("Unknown custom instruction: " + name));
	}
}
