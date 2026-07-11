package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.decl.Var;

public class VarEvaluator implements TemplateNodeEvaluator<Var> {
	@Override
	public void evaluate(TemplateEvaluationContext context, Var node) {
		context.declareVariable(node.getName(), node.getValue());
	}

	@Override
	public Reason validate(ValidationContext context, Var node) {
		if (node.getName() == null || node.getName().isBlank())
			return InvalidArgument.create("Var.name must not be blank");
		if (node.getType() == null || node.getType().isBlank())
			return InvalidArgument.create("Var.type must not be blank");
		return null;
	}
}
