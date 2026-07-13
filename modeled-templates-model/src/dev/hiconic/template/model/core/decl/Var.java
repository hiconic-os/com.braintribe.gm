package dev.hiconic.template.model.core.decl;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.TypeReference;
import dev.hiconic.template.model.core.instr.StatementInstructionNode;
import dev.hiconic.template.model.core.instr.VariableDefiningNode;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;

@PositionalArguments({"symbol", "type"})
public interface Var extends DeclarationNode, StatementInstructionNode, VariableDefiningNode {
	EntityType<Var> T = EntityTypes.T(Var.class);
	
	PropertyLiteral symbol = PropertyLiteral.of(T, "symbol");
	PropertyLiteral type = PropertyLiteral.of(T, "type");
	PropertyLiteral value = PropertyLiteral.of(T, "value");
	
	Symbol getSymbol();
	void setSymbol(Symbol symbol);

	TypeReference getType();
	void setType(TypeReference type);
	
	Object getValue();
	void setValue(Object value);
}
