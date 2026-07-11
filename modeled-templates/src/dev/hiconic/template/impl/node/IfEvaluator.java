package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.TypeCode;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.instr.If;

public class IfEvaluator implements TemplateNodeEvaluator<If> {
	@Override
	public void evaluate(TemplateEvaluationContext context, If node) {
		if (node.getCondition())
			context.evaluate(node.getBlock());
		else if (node.getElse() != null)
			context.evaluate(node.getElse());
	}

	@Override
	public Reason validate(ValidationContext context, If node) {
		if (node.getBlock() == null)
			return InvalidArgument.mandatoryPropertyNull(If.T, If.block.name());

		GenericModelType type = context.getType(node, If.condition);
		if (type == null || type.getTypeCode() != TypeCode.booleanType)
			return InvalidArgument.create("If.condition must evaluate to boolean, but evaluates to "
					+ (type == null ? "<unknown>" : type.getTypeSignature()));

		return null;
	}
}
