package dev.hiconic.template.impl.node;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.model.core.ErrorNode;

public class ErrorNodeEvaluator implements TemplateNodeEvaluator<ErrorNode> {
	@Override
	public void evaluate(TemplateEvaluationContext context, ErrorNode node) {
		context.append(node.getText());
	}
}
