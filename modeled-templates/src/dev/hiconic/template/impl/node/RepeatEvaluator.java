package dev.hiconic.template.impl.node;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import dev.hiconic.template.impl.parser.DefinitionTools;
import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.instr.Repeat;

public class RepeatEvaluator implements TemplateNodeEvaluator<Repeat> {
	@Override
	public GenericModelType expectedArgumentType(ValidationContext context, Repeat node, Property property) {
		return property == Repeat.count.property() ? SimpleTypes.TYPE_INTEGER : null;
	}

	@Override
	public Reason completeScope(ValidationContext context, Repeat node) {
		if (node.getIndexVariable() != null) {
			node.getIndexVariable().setType(DefinitionTools.type(SimpleTypes.TYPE_INTEGER.getTypeSignature()));
			node.getIndexVariable().setRequired(true);
			node.getIndexVariable().setMutable(false);
			node.setVariableDefinitions(List.of(node.getIndexVariable()));
		}
		return null;
	}

	@Override
	public void evaluate(TemplateEvaluationContext context, Repeat node) {
		int count = count(context, node);
		for (int i = 0; i < count; i++) {
			try {
				if (node.getIndexVariable() == null) {
					context.evaluate(node.getBlock());
				} else {
					Map<Symbol, Object> variables = new LinkedHashMap<>(1);
					variables.put(node.getIndexVariable().getSymbol(), i);
					context.withSymbolVariables(variables, () -> context.evaluate(node.getBlock()));
				}
			} catch (FlowControlSignal signal) {
				if (signal.kind() == FlowControlSignal.Kind.BREAK) break;
				if (signal.kind() == FlowControlSignal.Kind.CONTINUE) continue;
				throw signal;
			}
		}
	}

	@Override
	public Reason validate(ValidationContext context, Repeat node) {
		if (node.getBlock() == null)
			return InvalidArgument.mandatoryPropertyNull(Repeat.T, Repeat.block.name());
		GenericModelType type = context.getType(node, Repeat.count);
		if (type == null || !SimpleTypes.TYPE_INTEGER.isAssignableFrom(type))
			return InvalidArgument.create("Repeat.count must evaluate to integer, but evaluates to "
					+ (type == null ? "<unknown>" : type.getTypeSignature()));
		return null;
	}

	private static int count(TemplateEvaluationContext context, Repeat node) {
		ValueDescriptor descriptor = Repeat.count.property().getVdDirect(node);
		Object value = TemplateValues.evaluate(context,
				descriptor == null ? node.getCount() : context.evaluate(descriptor));
		if (value instanceof Integer count) return Math.max(0, count);
		throw new IllegalArgumentException("Repeat.count is not integer: " + value);
	}
}
