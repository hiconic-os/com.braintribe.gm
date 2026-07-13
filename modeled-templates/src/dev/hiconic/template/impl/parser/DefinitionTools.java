package dev.hiconic.template.impl.parser;

import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.TypeReference;
import dev.hiconic.template.model.core.decl.VariableDefinition;

public final class DefinitionTools {
	private DefinitionTools() {}
	public static Symbol symbol(String name) { Symbol value = Symbol.T.create(); value.setName(name); return value; }
	public static TypeReference type(String signature) { TypeReference value = TypeReference.T.create(); value.setTypeSignature(signature); return value; }
	public static VariableDefinition variable(String name, String type, boolean mutable) {
		VariableDefinition value = VariableDefinition.T.create();
		value.setSymbol(symbol(name));
		if (type != null) value.setType(type(type));
		value.setRequired(true);
		value.setMutable(mutable);
		return value;
	}
	public static String name(VariableDefinition value) { return value.getSymbol().getName(); }
	public static String type(VariableDefinition value) { return value.getType().getTypeSignature(); }
}
