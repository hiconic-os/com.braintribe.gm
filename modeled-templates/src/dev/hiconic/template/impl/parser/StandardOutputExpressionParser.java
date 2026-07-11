package dev.hiconic.template.impl.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateExpertRegistry;
import dev.hiconic.template.api.TransformerBinding;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.output.CssEscape;
import dev.hiconic.template.model.core.output.HtmlEsc;
import dev.hiconic.template.model.core.output.JavaLiteralEscape;
import dev.hiconic.template.model.core.output.JavaScriptEscape;
import dev.hiconic.template.model.core.output.JsonEscape;
import dev.hiconic.template.model.core.output.NoEscape;
import dev.hiconic.template.model.core.output.SafeOutput;
import dev.hiconic.template.model.core.output.Transformer;
import dev.hiconic.template.model.core.output.UrlComponentEscape;
import dev.hiconic.template.model.core.output.XmlEscape;
import dev.hiconic.template.model.core.vd.TransformValue;
import dev.hiconic.template.model.parse.TextRange;

public class StandardOutputExpressionParser {
	private final TemplateExpertRegistry registry;
	private final TemplateValueExpressionResolver resolver;
	private final ReflectedEntityNormalizer reflectedEntityNormalizer = new ReflectedEntityNormalizer();

	public StandardOutputExpressionParser(TemplateExpertRegistry registry, TemplateValueExpressionResolver resolver) {
		this.registry = registry;
		this.resolver = resolver;
	}

	public Maybe<OutputNode> parse(String expression, TextRange range) {
		List<String> pipe = splitPipe(expression);
		if (pipe.isEmpty() || pipe.get(0).trim().isEmpty())
			return Maybe.empty(ParseError.create("Output expression must not be empty"));

		Maybe<ParsedValueExpression> base = resolver.resolveValue(pipe.get(0).trim(), range);
		if (base.isUnsatisfied())
			return Maybe.empty(base.whyUnsatisfied());

		Build build = new Build(base.get().value(), base.get().type());
		for (int i = 1; i < pipe.size(); i++) {
			Maybe<Transformer> transformer = parseTransformer(pipe.get(i).trim(), range);
			if (transformer.isUnsatisfied())
				return Maybe.empty(transformer.whyUnsatisfied());
			Maybe<Build> transformed = applyExplicit(build, transformer.get());
			if (transformed.isUnsatisfied())
				return Maybe.empty(transformed.whyUnsatisfied());
			build = transformed.get();
		}

		Maybe<Build> completed = completeTo(build, SafeOutput.T);
		if (completed.isUnsatisfied())
			return Maybe.empty(completed.whyUnsatisfied());

		OutputNode output = OutputNode.T.create();
		Object value = completed.get().value();
		if (value instanceof ValueDescriptor) {
			ValueDescriptor descriptor = (ValueDescriptor) value;
			OutputNode.output.property().setVdDirect(output, descriptor);
		} else if (value instanceof SafeOutput) {
			SafeOutput safeOutput = (SafeOutput) value;
			output.setOutput(safeOutput);
		} else {
			return Maybe.empty(ParseError.create("Output expression does not evaluate to SafeOutput"));
		}

		return Maybe.complete(output);
	}

	private Maybe<Build> applyExplicit(Build build, Transformer transformer) {
		TransformerBinding binding = registry.findTransformer(transformer.entityType(), build.type());
		if (binding == null) {
			Maybe<Build> adapted = adaptToTransformerInput(build, transformer.entityType());
			if (adapted.isUnsatisfied())
				return Maybe.empty(adapted.whyUnsatisfied());
			build = adapted.get();
			binding = registry.findTransformer(transformer.entityType(), build.type());
		}

		if (binding == null)
			return Maybe.empty(ParseError.create("No transformer registered for "
					+ transformer.entityType().getTypeSignature() + " accepting " + build.type().getTypeSignature()));

		return Maybe.complete(apply(build, transformer, binding));
	}

	private Maybe<Build> adaptToTransformerInput(Build build, EntityType<?> transformerType) {
		for (TransformerBinding binding : registry.findTransformers(transformerType)) {
			Maybe<Build> candidate = completeTo(build, binding.inputType());
			if (candidate.isSatisfied())
				return candidate;
		}
		return Maybe.empty(ParseError.create("Cannot adapt " + build.type().getTypeSignature()
				+ " to transformer " + transformerType.getTypeSignature()));
	}

	private Maybe<Build> completeTo(Build build, GenericModelType targetType) {
		if (targetType.isAssignableFrom(build.type()))
			return Maybe.complete(build);

		List<TransformerBinding> chain = defaultChain(build.type(), targetType);
		if (chain == null)
			return Maybe.empty(ParseError.create("Cannot find default transformer chain from "
					+ build.type().getTypeSignature() + " to " + targetType.getTypeSignature()));

		for (TransformerBinding binding : chain)
			build = apply(build, binding.transformerType().create(), binding);
		return Maybe.complete(build);
	}

	private Build apply(Build build, Transformer transformer, TransformerBinding binding) {
		TransformValue transform = TransformValue.T.create();
		transform.setTransformer(transformer);
		if (build.value() instanceof ValueDescriptor) {
			ValueDescriptor descriptor = (ValueDescriptor) build.value();
			TransformValue.input.property().setVdDirect(transform, descriptor);
		} else {
			transform.setInput(build.value());
		}
		transform.setInputTypeSignature(build.type().getTypeSignature());
		transform.setTypeSignature(binding.outputType().getTypeSignature());
		return new Build(transform, binding.outputType());
	}

	private List<TransformerBinding> defaultChain(GenericModelType source, GenericModelType target) {
		Queue<Path> queue = new ArrayDeque<>();
		Map<GenericModelType, Boolean> visited = new IdentityHashMap<>();
		queue.add(new Path(source, new ArrayList<TransformerBinding>()));
		visited.put(source, Boolean.TRUE);

		while (!queue.isEmpty()) {
			Path path = queue.remove();
			for (TransformerBinding binding : registry.defaultTransformers()) {
				if (!binding.inputType().isAssignableFrom(path.type()))
					continue;

				List<TransformerBinding> nextChain = new ArrayList<>(path.chain());
				nextChain.add(binding);
				GenericModelType nextType = binding.outputType();
				if (target.isAssignableFrom(nextType))
					return nextChain;

				if (visited.put(nextType, Boolean.TRUE) == null)
					queue.add(new Path(nextType, nextChain));
			}
		}
		return null;
	}

	private Maybe<Transformer> parseTransformer(String specification, TextRange range) {
		if (specification.trim().isEmpty())
			return Maybe.empty(ParseError.create("Empty transformer in output pipe"));

		String name = firstToken(specification);
		String arguments = specification.substring(name.length()).trim();
		EntityType<? extends Transformer> type = resolveTransformerType(name);
		if (type == null)
			return Maybe.empty(ParseError.create("Unknown transformer: " + name));

		Maybe<List<ParsedArgument>> parsedArguments = parseArguments(arguments, range);
		if (parsedArguments.isUnsatisfied())
			return Maybe.empty(parsedArguments.whyUnsatisfied());
		Maybe<? extends Transformer> transformer = reflectedEntityNormalizer.normalize(type, parsedArguments.get());
		if (transformer.isUnsatisfied())
			return Maybe.empty(transformer.whyUnsatisfied());
		return Maybe.complete(transformer.get());
	}

	private EntityType<? extends Transformer> resolveTransformerType(String name) {
		EntityType<? extends Transformer> shortcut = shortcutAliases().get(name.toLowerCase(Locale.ROOT));
		if (shortcut != null)
			return shortcut;

		List<EntityType<? extends Transformer>> matches = new ArrayList<>();
		for (EntityType<? extends Transformer> type : registry.transformerTypes()) {
			if (name.equals(type.getTypeSignature()) || name.equals(type.getShortName())
					|| name.equals(TemplateTypeNameResolver.toLowerKebabCase(type.getShortName())))
				matches.add(type);
		}
		return matches.size() == 1 ? matches.get(0) : null;
	}

	private static Map<String, EntityType<? extends Transformer>> shortcutAliases() {
		Map<String, EntityType<? extends Transformer>> aliases = new HashMap<>();
		aliases.put("html-esc", HtmlEsc.T);
		aliases.put("html-escape", HtmlEsc.T);
		aliases.put("xml-esc", XmlEscape.T);
		aliases.put("xml-escape", XmlEscape.T);
		aliases.put("java-literal-esc", JavaLiteralEscape.T);
		aliases.put("java-literal-escape", JavaLiteralEscape.T);
		aliases.put("js-esc", JavaScriptEscape.T);
		aliases.put("javascript-esc", JavaScriptEscape.T);
		aliases.put("json-esc", JsonEscape.T);
		aliases.put("css-esc", CssEscape.T);
		aliases.put("url-esc", UrlComponentEscape.T);
		aliases.put("url-component-esc", UrlComponentEscape.T);
		aliases.put("no-esc", NoEscape.T);
		return aliases;
	}

	private Maybe<List<ParsedArgument>> parseArguments(String source, TextRange range) {
		List<ParsedArgument> arguments = new ArrayList<>();
		List<String> tokens = tokenize(source);
		for (int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			String name = null;
			String valueSource = token;
			if (token.startsWith("--")) {
				name = toCamelCase(token.substring(2));
				if (++i >= tokens.size())
					return Maybe.empty(ParseError.create("Missing value for argument " + token));
				valueSource = tokens.get(i);
			} else {
				int equals = token.indexOf('=');
				if (equals > 0) {
					name = token.substring(0, equals);
					valueSource = token.substring(equals + 1);
				}
			}

			Maybe<ParsedValueExpression> value = resolver.resolveArgumentValue(valueSource, range);
			if (value.isUnsatisfied())
				return Maybe.empty(value.whyUnsatisfied());
			arguments.add(new ParsedArgument(name, value.get().value(), value.get().type()));
		}
		return Maybe.complete(arguments);
	}

	private static List<String> splitPipe(String expression) {
		List<String> result = new ArrayList<>();
		int start = 0;
		char quote = 0;
		int parentheses = 0;
		int brackets = 0;
		int braces = 0;
		for (int i = 0; i < expression.length(); i++) {
			char ch = expression.charAt(i);
			if (quote != 0) {
				if (ch == quote && !isEscaped(expression, i))
					quote = 0;
			} else if (ch == '"' || ch == '\'') {
				quote = ch;
			} else if (ch == '(') {
				parentheses++;
			} else if (ch == ')') {
				parentheses = Math.max(0, parentheses - 1);
			} else if (ch == '[') {
				brackets++;
			} else if (ch == ']') {
				brackets = Math.max(0, brackets - 1);
			} else if (ch == '{') {
				braces++;
			} else if (ch == '}') {
				braces = Math.max(0, braces - 1);
			} else if (ch == '|' && parentheses == 0 && brackets == 0 && braces == 0) {
				result.add(expression.substring(start, i));
				start = i + 1;
			}
		}
		result.add(expression.substring(start));
		return result;
	}

	private static String firstToken(String text) {
		int index = 0;
		while (index < text.length() && !Character.isWhitespace(text.charAt(index)))
			index++;
		return text.substring(0, index);
	}

	private static List<String> tokenize(String source) {
		List<String> tokens = new ArrayList<>();
		int start = -1;
		char quote = 0;
		int parentheses = 0;
		int brackets = 0;
		int braces = 0;
		for (int i = 0; i < source.length(); i++) {
			char ch = source.charAt(i);
			if (start < 0 && Character.isWhitespace(ch))
				continue;
			if (start < 0)
				start = i;
			if (quote != 0) {
				if (ch == quote && !isEscaped(source, i))
					quote = 0;
			} else if (ch == '"' || ch == '\'') {
				quote = ch;
			} else if (ch == '(') {
				parentheses++;
			} else if (ch == ')') {
				parentheses = Math.max(0, parentheses - 1);
			} else if (ch == '[') {
				brackets++;
			} else if (ch == ']') {
				brackets = Math.max(0, brackets - 1);
			} else if (ch == '{') {
				braces++;
			} else if (ch == '}') {
				braces = Math.max(0, braces - 1);
			} else if (Character.isWhitespace(ch) && parentheses == 0 && brackets == 0 && braces == 0) {
				tokens.add(source.substring(start, i));
				start = -1;
			}
		}
		if (start >= 0)
			tokens.add(source.substring(start));
		return tokens;
	}

	private static boolean isEscaped(String text, int index) {
		int backslashes = 0;
		for (int i = index - 1; i >= 0 && text.charAt(i) == '\\'; i--)
			backslashes++;
		return (backslashes & 1) == 1;
	}

	private static String toCamelCase(String name) {
		StringBuilder result = new StringBuilder(name.length());
		boolean upper = false;
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (ch == '-') {
				upper = true;
			} else if (upper) {
				result.append(Character.toUpperCase(ch));
				upper = false;
			} else {
				result.append(ch);
			}
		}
		return result.toString();
	}

	private static final class Build {
		private final Object value;
		private final GenericModelType type;

		private Build(Object value, GenericModelType type) {
			this.value = value;
			this.type = type;
		}

		private Object value() {
			return value;
		}

		private GenericModelType type() {
			return type;
		}
	}

	private static final class Path {
		private final GenericModelType type;
		private final List<TransformerBinding> chain;

		private Path(GenericModelType type, List<TransformerBinding> chain) {
			this.type = type;
			this.chain = chain;
		}

		private GenericModelType type() {
			return type;
		}

		private List<TransformerBinding> chain() {
			return chain;
		}
	}
}
