package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.Variable;
import dev.hiconic.template.model.core.Symbol;

public interface TemplateVariable extends Variable {
	EntityType<TemplateVariable> T = EntityTypes.T(TemplateVariable.class);
	Symbol getSymbol();
	void setSymbol(Symbol symbol);
}
