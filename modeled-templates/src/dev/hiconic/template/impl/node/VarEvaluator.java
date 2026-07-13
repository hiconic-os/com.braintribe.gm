package dev.hiconic.template.impl.node;

import java.util.List;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.TemplateValues;
import dev.hiconic.template.model.core.decl.Var;
import dev.hiconic.template.model.core.decl.VariableDefinition;
import static dev.hiconic.template.impl.parser.DefinitionTools.*;

public class VarEvaluator implements TemplateNodeEvaluator<Var> {
	@Override
	public GenericModelType expectedArgumentType(ValidationContext context, Var node, Property property) {
		return property == Var.value.property() && node.getType() != null
				? context.resolveType(node.getType().getTypeSignature()) : null;
	}

	@Override
	public Reason complete(ValidationContext context, Var node) {
		if (node.getSymbol() == null)
			return InvalidArgument.create("Var.symbol must not be null");
		if (node.getType() == null) {
			GenericModelType inferred = context.getType(node, Var.value);
			if (inferred == null) return InvalidArgument.create("Cannot infer Var type without a typed value");
			node.setType(dev.hiconic.template.impl.parser.DefinitionTools.type(inferred.getTypeSignature()));
		}
		VariableDefinition definition = variable(node.getSymbol().getName(), node.getType().getTypeSignature(), true);
		definition.setSymbol(node.getSymbol());
		node.setVariableDefinitions(List.of(definition));
		return validate(context, node);
	}

	@Override
	public void evaluate(TemplateEvaluationContext context, Var node) {
		ValueDescriptor descriptor = Var.value.property().getVdDirect(node);
		Object value = descriptor == null ? node.getValue() : context.evaluate(descriptor);
		context.declareVariable(node.getSymbol(), TemplateValues.evaluate(context, value));
	}

	@Override
	public Reason validate(ValidationContext context, Var node) {
		if (node.getSymbol() == null || node.getSymbol().getName() == null || node.getSymbol().getName().isBlank())
			return InvalidArgument.create("Var.name must not be blank");
		if (node.getType() == null || node.getType().getTypeSignature() == null || node.getType().getTypeSignature().isBlank())
			return InvalidArgument.create("Var.type must not be blank");
		GenericModelType declaredType = context.resolveType(node.getType().getTypeSignature());
		if (declaredType == null)
			return InvalidArgument.create("Unknown variable type: " + node.getType().getTypeSignature());
		ValueDescriptor descriptor = Var.value.property().getVdDirect(node);
		Object directValue = Var.value.property().getDirect(node);
		if (descriptor != null || directValue != null || context.isExplicitNull(node, Var.value)) {
			if (descriptor == null && context.isExplicitNull(node, Var.value)) return null;
			GenericModelType valueType = context.getType(node, Var.value);
			if (valueType == null || !declaredType.isAssignableFrom(valueType))
				return InvalidArgument.create("Var.value is not assignable to " + node.getType().getTypeSignature());
		}
		return null;
	}
}
