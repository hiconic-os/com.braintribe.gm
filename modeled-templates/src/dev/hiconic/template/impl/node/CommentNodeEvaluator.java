package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.CommentNode;

public class CommentNodeEvaluator implements TemplateNodeEvaluator<CommentNode> {
	@Override
	public void evaluate(TemplateEvaluationContext context, CommentNode node) {
		// intentionally silent
	}

	@Override
	public Reason validate(ValidationContext context, CommentNode node) {
		return null;
	}
}
