package dev.hiconic.template.impl.parser;

import java.util.HashMap;
import java.util.Map;
import dev.hiconic.template.model.core.Symbol;

public final class TemplateSymbolTable {
	private final Map<String, Symbol> symbols = new HashMap<>();
	public Symbol intern(String name) {
		return symbols.computeIfAbsent(name, key -> {
			Symbol symbol = Symbol.T.create();
			symbol.setName(key);
			return symbol;
		});
	}
}
