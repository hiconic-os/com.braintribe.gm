package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.output.SafeOutput;

public class OutputNodeEvaluator implements TemplateNodeEvaluator<OutputNode> {
	@Override
	public void evaluate(TemplateEvaluationContext context, OutputNode node) {
		ValueDescriptor descriptor = OutputNode.output.property().getVdDirect(node);
		SafeOutput output = descriptor == null ? node.getOutput() : (SafeOutput) context.evaluate(descriptor);
		if (output != null)
			context.append(output.getText());
	}

	@Override
	public Reason validate(ValidationContext context, OutputNode node) {
		GenericModelType type = context.getType(node, OutputNode.output);
		if (type == null || !SafeOutput.T.isAssignableFrom(type))
			return InvalidArgument.create("OutputNode.output must evaluate to SafeOutput, but evaluates to "
					+ (type == null ? "<unknown>" : type.getTypeSignature()));
		return null;
	}
}
