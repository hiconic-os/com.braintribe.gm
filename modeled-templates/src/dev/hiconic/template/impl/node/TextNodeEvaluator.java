package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.TextNode;

public class TextNodeEvaluator implements TemplateNodeEvaluator<TextNode> {
	@Override
	public void evaluate(TemplateEvaluationContext context, TextNode node) {
		context.append(node.getText());
	}

	@Override
	public Reason validate(ValidationContext context, TextNode node) {
		return node.getText() == null ? InvalidArgument.mandatoryPropertyNull(TextNode.T, TextNode.text.name()) : null;
	}
}
