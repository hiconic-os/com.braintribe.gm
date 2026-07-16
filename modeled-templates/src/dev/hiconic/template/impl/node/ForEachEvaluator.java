package dev.hiconic.template.impl.node;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.generic.value.ValueDescriptor;
import java.util.List;
import dev.hiconic.template.impl.parser.DefinitionTools;
import dev.hiconic.template.impl.TemplateValues;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.instr.ForEach;
import static dev.hiconic.template.impl.parser.DefinitionTools.name;

public class ForEachEvaluator implements TemplateNodeEvaluator<ForEach> {
	@Override
	public Reason completeScope(ValidationContext context, ForEach node) {
		GenericModelType type = context.getType(node, ForEach.iterable);
		if (!(type instanceof CollectionType collectionType) || collectionType.getCollectionKind() == CollectionType.CollectionKind.map)
			return InvalidArgument.create("ForEach.iterable must have a statically known collection type, but has "
					+ (type == null ? "<unknown>" : type.getTypeSignature()));
		if (node.getVariable() == null) node.setVariable(DefinitionTools.variable("_", null, false));
		node.getVariable().setType(DefinitionTools.type(collectionType.getCollectionElementType().getTypeSignature()));
		node.getVariable().setRequired(true);
		node.getVariable().setMutable(false);
		if (node.getIndexVariable() != null) {
			node.getIndexVariable().setType(DefinitionTools.type(SimpleTypes.TYPE_INTEGER.getTypeSignature()));
			node.getIndexVariable().setRequired(true);
			node.getIndexVariable().setMutable(false);
			node.setVariableDefinitions(List.of(node.getVariable(), node.getIndexVariable()));
		} else node.setVariableDefinitions(List.of(node.getVariable()));
		return null;
	}
	@Override
	public void evaluate(TemplateEvaluationContext context, ForEach node) {
		ValueDescriptor descriptor = ForEach.iterable.property().getVdDirect(node);
		Object iterable = TemplateValues.evaluate(context,
				descriptor == null ? node.getIterable() : context.evaluate(descriptor));
		Iterator<?> iterator = iterator(iterable);
		if (!iterator.hasNext()) {
			if (node.getEmpty() != null)
				context.withVariables(Map.of(), () -> context.evaluate(node.getEmpty()));
			return;
		}

		int index = 0;
		while (iterator.hasNext()) {
			Map<dev.hiconic.template.model.core.Symbol, Object> variables = new LinkedHashMap<>(2);
			variables.put(node.getVariable().getSymbol(), iterator.next());
			if (node.getIndexVariable() != null)
				variables.put(node.getIndexVariable().getSymbol(), index);
			try {
				context.withSymbolVariables(variables, () -> context.evaluate(node.getBlock()));
			} catch (FlowControlSignal signal) {
				if (signal.kind() == FlowControlSignal.Kind.BREAK) break;
				if (signal.kind() == FlowControlSignal.Kind.CONTINUE) {
					index++;
					continue;
				}
				throw signal;
			}
			index++;
		}
	}

	@Override
	public Reason validate(ValidationContext context, ForEach node) {
		if (node.getVariable() == null || node.getVariable().getSymbol() == null || name(node.getVariable()).isBlank())
			return InvalidArgument.create("ForEach.variable must not be blank");
		if (node.getBlock() == null)
			return InvalidArgument.mandatoryPropertyNull(ForEach.T, ForEach.block.name());

		GenericModelType type = context.getType(node, ForEach.iterable);
		if (!(type instanceof CollectionType collectionType)
				|| collectionType.getCollectionKind() == CollectionType.CollectionKind.map)
			return InvalidArgument.create("ForEach.iterable must evaluate to a collection, but evaluates to "
					+ (type == null ? "<unknown>" : type.getTypeSignature()));

		return null;
	}

	private static Iterator<?> iterator(Object value) {
		if (value instanceof Iterable<?> iterable)
			return iterable.iterator();
		if (value != null && value.getClass().isArray())
			return new Iterator<>() {
				private final int length = Array.getLength(value);
				private int index;

				@Override
				public boolean hasNext() {
					return index < length;
				}

				@Override
				public Object next() {
					return Array.get(value, index++);
				}
			};
		throw new IllegalArgumentException("ForEach.iterable is not iterable: "
				+ (value == null ? "null" : value.getClass().getName()));
	}
}
