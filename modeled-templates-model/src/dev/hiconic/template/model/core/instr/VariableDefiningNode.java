package dev.hiconic.template.model.core.instr;

import java.util.List;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.decl.VariableDefinition;

/** A directive whose completion publishes statically visible variable definitions. */
@Abstract
public interface VariableDefiningNode extends DirectiveNode {
	EntityType<VariableDefiningNode> T = EntityTypes.T(VariableDefiningNode.class);
	PropertyLiteral variableDefinitions = PropertyLiteral.of(T, "variableDefinitions");

	List<VariableDefinition> getVariableDefinitions();
	void setVariableDefinitions(List<VariableDefinition> variableDefinitions);
}
