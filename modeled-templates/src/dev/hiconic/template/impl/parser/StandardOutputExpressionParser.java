package dev.hiconic.template.impl.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.enhance.EnhancedEntity;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateExpertRegistry;
import dev.hiconic.template.api.ValueConversionBinding;
import dev.hiconic.template.impl.EvaluationPai;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.output.SafeOutput;
import dev.hiconic.template.model.core.vd.UnaryOperation;
import dev.hiconic.template.model.parse.TextRange;

/** Builds OutputNode through the same general expression parser used everywhere else. */
public class StandardOutputExpressionParser {
	private final TemplateExpertRegistry registry;
	private final TemplateValueExpressionResolver resolver;

	public StandardOutputExpressionParser(TemplateExpertRegistry registry, TemplateValueExpressionResolver resolver) {
		this.registry = registry;
		this.resolver = resolver;
	}

	public Maybe<OutputNode> parse(String expression, TextRange range) {
		Maybe<ParsedValueExpression> parsed = resolver.resolveValue(expression, range);
		if (parsed.isUnsatisfied()) return Maybe.empty(parsed.whyUnsatisfied());
		if (NullLiteral.is(parsed.get().value())) {
			OutputNode output = OutputNode.T.create();
			output.setOutput(null);
			return Maybe.complete(output);
		}
		Maybe<ParsedValueExpression> completed = completeTo(parsed.get(), SafeOutput.T);
		if (completed.isUnsatisfied()) return Maybe.empty(completed.whyUnsatisfied());
		OutputNode output = OutputNode.T.create();
		Object value = completed.get().value();
		if (value instanceof ValueDescriptor descriptor)
			OutputNode.output.property().setVdDirect(output, descriptor);
		else if (value instanceof SafeOutput safe)
			output.setOutput(safe);
		else return Maybe.empty(ParseError.create("Output expression does not evaluate to SafeOutput"));
		return Maybe.complete(output);
	}

	private Maybe<ParsedValueExpression> completeTo(ParsedValueExpression value, GenericModelType targetType) {
		if (value.type() != null && targetType.isAssignableFrom(value.type())) return Maybe.complete(value);
		List<ValueConversionBinding> chain = defaultChain(value.type(), targetType);
		if (chain == null) return Maybe.empty(ParseError.create("Cannot find default value conversion from "
				+ (value.type() == null ? "<unknown>" : value.type().getTypeSignature()) + " to " + targetType.getTypeSignature()));
		ParsedValueExpression current = value;
		for (ValueConversionBinding binding : chain) current = apply(current, binding);
		return Maybe.complete(current);
	}

	private ParsedValueExpression apply(ParsedValueExpression input, ValueConversionBinding binding) {
		ValueDescriptor descriptor = binding.descriptorType().create();
		if (!(descriptor instanceof UnaryOperation unary))
			throw new IllegalStateException("Default conversion VD has no unary operand: " + binding.descriptorType());
		if (input.value() instanceof ValueDescriptor vd)
			UnaryOperation.operand.property().setVdDirect(unary, vd);
		else unary.setOperand(input.value());
		unary.setInputType(DefinitionTools.type(input.type().getTypeSignature()));
		unary.setTypeSignature(binding.outputType().getTypeSignature());
		if (descriptor instanceof EnhancedEntity enhanced) enhanced.pushPai(new EvaluationPai());
		return new ParsedValueExpression(descriptor, binding.outputType());
	}

	private List<ValueConversionBinding> defaultChain(GenericModelType source, GenericModelType target) {
		if (source == null) return null;
		Queue<Path> queue = new ArrayDeque<>();
		Map<GenericModelType, Boolean> visited = new IdentityHashMap<>();
		queue.add(new Path(source, new ArrayList<>()));
		visited.put(source, Boolean.TRUE);
		while (!queue.isEmpty()) {
			Path path = queue.remove();
			for (ValueConversionBinding binding : registry.defaultConversions()) {
				if (!binding.inputType().isAssignableFrom(path.type)) continue;
				List<ValueConversionBinding> next = new ArrayList<>(path.chain);
				next.add(binding);
				if (target.isAssignableFrom(binding.outputType())) return next;
				if (visited.put(binding.outputType(), Boolean.TRUE) == null)
					queue.add(new Path(binding.outputType(), next));
			}
		}
		return null;
	}

	private static final class Path {
		final GenericModelType type;
		final List<ValueConversionBinding> chain;
		Path(GenericModelType type, List<ValueConversionBinding> chain) { this.type = type; this.chain = chain; }
	}
}
