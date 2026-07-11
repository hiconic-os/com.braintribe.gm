package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.instr.Set;

public class SetEvaluator implements TemplateNodeEvaluator<Set> {
	@Override
	public void evaluate(TemplateEvaluationContext context, Set node) {
		context.setVariable(node.getVar().getName(), node.getValue());
	}

	@Override
	public Reason validate(ValidationContext context, Set node) {
		if (node.getVar() == null)
			return InvalidArgument.mandatoryPropertyNull(Set.T, Set.var.name());

		GenericModelType valueType = context.getType(node, Set.value);
		GenericModelType variableType = node.getVar().valueType();
		if (valueType == null || !variableType.isAssignableFrom(valueType))
			return InvalidArgument.create("Set.value type "
					+ (valueType == null ? "<unknown>" : valueType.getTypeSignature())
					+ " is not assignable to variable " + node.getVar().getName()
					+ " of type " + variableType.getTypeSignature());

		return null;
	}
}
