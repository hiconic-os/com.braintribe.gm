package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.TemplateValues;
import dev.hiconic.template.model.core.instr.While;

public class WhileEvaluator implements TemplateNodeEvaluator<While> {
	@Override
	public GenericModelType expectedArgumentType(ValidationContext context, While node, Property property) {
		return property == While.condition.property() ? SimpleTypes.TYPE_BOOLEAN : null;
	}

	@Override
	public void evaluate(TemplateEvaluationContext context, While node) {
		while (condition(context, node)) {
			try {
				context.evaluate(node.getBlock());
			} catch (FlowControlSignal signal) {
				if (signal.kind() == FlowControlSignal.Kind.BREAK) break;
				if (signal.kind() == FlowControlSignal.Kind.CONTINUE) continue;
				throw signal;
			}
		}
	}

	@Override
	public Reason validate(ValidationContext context, While node) {
		if (node.getBlock() == null)
			return InvalidArgument.mandatoryPropertyNull(While.T, While.block.name());
		GenericModelType type = context.getType(node, While.condition);
		return type != null && SimpleTypes.TYPE_BOOLEAN.isAssignableFrom(type) ? null : InvalidArgument.create(
				"While.condition must evaluate to boolean, but evaluates to "
						+ (type == null ? "<unknown>" : type.getTypeSignature()));
	}

	private static boolean condition(TemplateEvaluationContext context, While node) {
		ValueDescriptor descriptor = While.condition.property().getVdDirect(node);
		Object value = TemplateValues.evaluate(context,
				descriptor == null ? node.getCondition() : context.evaluate(descriptor));
		if (value instanceof Boolean condition) return condition;
		throw new IllegalArgumentException("While.condition is not boolean: " + value);
	}
}
