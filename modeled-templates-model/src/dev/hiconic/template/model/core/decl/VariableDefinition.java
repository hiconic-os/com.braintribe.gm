package dev.hiconic.template.model.core.decl;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.TypeReference;

@PositionalArguments({"symbol", "type"})
public interface VariableDefinition extends GenericEntity {
	EntityType<VariableDefinition> T = EntityTypes.T(VariableDefinition.class);
	PropertyLiteral symbol = PropertyLiteral.of(T, "symbol");
	PropertyLiteral type = PropertyLiteral.of(T, "type");
	PropertyLiteral required = PropertyLiteral.of(T, "required");
	PropertyLiteral defaultValue = PropertyLiteral.of(T, "default");
	PropertyLiteral mutable = PropertyLiteral.of(T, "mutable");

	Symbol getSymbol();
	void setSymbol(Symbol symbol);
	TypeReference getType();
	void setType(TypeReference type);
	@Initializer("true")
	boolean getRequired();
	void setRequired(boolean required);
	Object getDefault();
	void setDefault(Object defaultValue);
	boolean getMutable();
	void setMutable(boolean mutable);
}
