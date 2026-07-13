package dev.hiconic.template.impl.node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.TemplateValues;
import dev.hiconic.template.impl.parser.DefinitionTools;
import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.decl.VariableDefinition;
import dev.hiconic.template.model.core.instr.ForEachEntry;

public class ForEachEntryEvaluator implements TemplateNodeEvaluator<ForEachEntry> {
	@Override
	public Reason completeScope(ValidationContext context, ForEachEntry node) {
		GenericModelType type = context.getType(node, ForEachEntry.iterable);
		if (!(type instanceof MapType mapType))
			return InvalidArgument.create("ForEachEntry.iterable must have a statically known map type, but has "
					+ (type == null ? "<unknown>" : type.getTypeSignature()));
		ArrayList<VariableDefinition> definitions = new ArrayList<>(2);
		complete(node.getKey(), mapType.getKeyType(), definitions);
		complete(node.getValue(), mapType.getValueType(), definitions);
		node.setVariableDefinitions(definitions);
		return null;
	}

	private static void complete(VariableDefinition definition, GenericModelType type,
			ArrayList<VariableDefinition> definitions) {
		if (definition == null) return;
		definition.setType(DefinitionTools.type(type.getTypeSignature()));
		definition.setRequired(true);
		definition.setMutable(false);
		definitions.add(definition);
	}

	@Override
	public void evaluate(TemplateEvaluationContext context, ForEachEntry node) {
		ValueDescriptor descriptor = ForEachEntry.iterable.property().getVdDirect(node);
		Object value = TemplateValues.evaluate(context,
				descriptor == null ? node.getIterable() : context.evaluate(descriptor));
		if (!(value instanceof Map<?, ?> map))
			throw new IllegalArgumentException("ForEachEntry.iterable is not a map");
		if (map.isEmpty()) {
			if (node.getEmpty() != null)
				context.withVariables(Map.of(), () -> context.evaluate(node.getEmpty()));
			return;
		}
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Map<Symbol, Object> variables = new LinkedHashMap<>(2);
			if (node.getKey() != null) variables.put(node.getKey().getSymbol(), entry.getKey());
			if (node.getValue() != null) variables.put(node.getValue().getSymbol(), entry.getValue());
			context.withSymbolVariables(variables, () -> context.evaluate(node.getBlock()));
		}
	}

	@Override
	public Reason validate(ValidationContext context, ForEachEntry node) {
		if (node.getBlock() == null)
			return InvalidArgument.mandatoryPropertyNull(ForEachEntry.T, ForEachEntry.block.name());
		GenericModelType type = context.getType(node, ForEachEntry.iterable);
		return type instanceof MapType ? null : InvalidArgument.create(
				"ForEachEntry.iterable must evaluate to a map, but evaluates to "
						+ (type == null ? "<unknown>" : type.getTypeSignature()));
	}
}
