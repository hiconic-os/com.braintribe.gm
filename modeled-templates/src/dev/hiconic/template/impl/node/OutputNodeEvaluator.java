package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.output.Output;

public class OutputNodeEvaluator implements TemplateNodeEvaluator<OutputNode> {
	@Override
	public void evaluate(TemplateEvaluationContext context, OutputNode node) {
		ValueDescriptor descriptor = OutputNode.output.property().getVdDirect(node);
		Output output = descriptor == null ? node.getOutput() : (Output) context.evaluate(descriptor);
		// Sink-agnostic: the context decides how to emit (text append, document run, ...). The
		// admissible output kinds for the active sink are enforced at validation time.
		if (output != null)
			context.emit(output);
	}

	@Override
	public Reason validate(ValidationContext context, OutputNode node) {
		GenericModelType type = context.getType(node, OutputNode.output);
		if (type == null || !Output.T.isAssignableFrom(type))
			return InvalidArgument.create("OutputNode.output must evaluate to an Output, but evaluates to "
					+ (type == null ? "<unknown>" : type.getTypeSignature()));

		// A node with neither a bound value descriptor nor a direct value emits nothing (e.g. the
		// null literal in ${null}); such a no-op output is valid for any sink, regardless of the
		// property's declared (now widened) type.
		if (OutputNode.output.property().getVdDirect(node) == null && node.getOutput() == null)
			return null;

		GenericModelType supported = context.supportedOutputType();
		if (!supported.isAssignableFrom(type))
			return InvalidArgument.create("The active output sink does not support " + type.getTypeSignature()
					+ " (it supports " + supported.getTypeSignature() + ")");
		return null;
	}
}
