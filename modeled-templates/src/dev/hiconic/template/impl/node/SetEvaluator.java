package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.TemplateValues;
import dev.hiconic.template.model.core.instr.PropertyAssignmentTarget;
import dev.hiconic.template.model.core.instr.Set;
import dev.hiconic.template.model.core.instr.VariableAssignmentTarget;

public class SetEvaluator implements TemplateNodeEvaluator<Set> {
	@Override
	public GenericModelType expectedArgumentType(ValidationContext context, Set node, Property property) {
		return property == Set.value.property() && node.getVar() != null
				? context.resolveType(node.getVar().getTypeSignature()) : null;
	}

	@Override
	public void evaluate(TemplateEvaluationContext context, Set node) {
		ValueDescriptor descriptor = Set.value.property().getVdDirect(node);
		Object value = TemplateValues.evaluate(context,
				descriptor == null ? node.getValue() : context.evaluate(descriptor));
		if (node.getVar() instanceof VariableAssignmentTarget target)
			context.setVariable(target.getSymbol(), value);
		else if (node.getVar() instanceof PropertyAssignmentTarget target)
			context.setProperty(target.getPath(), value);
		else throw new IllegalArgumentException("Unsupported assignment target: " + node.getVar());
	}

	@Override
	public Reason validate(ValidationContext context, Set node) {
		if (node.getVar() == null)
			return InvalidArgument.mandatoryPropertyNull(Set.T, Set.var.name());

		ValueDescriptor descriptor = Set.value.property().getVdDirect(node);
		if (descriptor == null && context.isExplicitNull(node, Set.value))
			return node.getVar().getNullable() ? null
					: InvalidArgument.create("Set.value cannot be null because the assignment target is not nullable");
		GenericModelType valueType = context.getType(node, Set.value);
		GenericModelType variableType = context.resolveType(node.getVar().getTypeSignature());
		if (valueType == null || !variableType.isAssignableFrom(valueType))
			return InvalidArgument.create("Set.value type "
					+ (valueType == null ? "<unknown>" : valueType.getTypeSignature())
					+ " is not assignable to assignment target"
					+ " of type " + variableType.getTypeSignature());

		return null;
	}
}
