package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.ArgumentedNode;

public class ArgumentedNodeEvaluator implements TemplateNodeEvaluator<ArgumentedNode> {
	@Override
	public void evaluate(TemplateEvaluationContext context, ArgumentedNode node) {
		context.evaluate(node.getNode());
	}

	@Override
	public Reason validate(ValidationContext context, ArgumentedNode node) {
		return node.getNode() == null ? InvalidArgument.mandatoryPropertyNull(ArgumentedNode.T, ArgumentedNode.node.name()) : null;
	}
}
