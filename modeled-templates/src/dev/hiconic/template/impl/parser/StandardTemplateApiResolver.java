package dev.hiconic.template.impl.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.bvd.navigation.PropertyPath;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.meta.cmd.CmdResolver;

import dev.hiconic.template.api.TemplateExpertRegistry;
import dev.hiconic.template.api.TemplateParserResolver;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.ConfigurableTemplateExpertRegistry;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.decl.Parameter;
import dev.hiconic.template.model.core.instr.ForEach;
import dev.hiconic.template.model.core.instr.If;
import dev.hiconic.template.model.core.instr.InstructionNode;
import dev.hiconic.template.model.core.instr.Switch;
import dev.hiconic.template.model.parse.TextRange;

public class StandardTemplateApiResolver implements TemplateParserResolver, TemplateValueExpressionResolver, ValidationContext {
	private final TemplateExpertRegistry registry;
	private final GenericModelType rootType;
	private final String rootVariable;
	private final StandardLiteralParser literalParser;
	private final StandardOutputExpressionParser outputParser;
	private final TemplateValidationScope validationScope;
	private final ExpertModelCompleter completer;
	private final ReflectedEntityNormalizer reflectedEntityNormalizer = new ReflectedEntityNormalizer();
	private final ReflectedInstructionNormalizer reflectedInstructionNormalizer = new ReflectedInstructionNormalizer();
	private final TemplateDeclarationScope declarationScope = new TemplateDeclarationScope();
	private final RuntimeInstructionNormalizer runtimeInstructionNormalizer = new RuntimeInstructionNormalizer(this);
	private final TemplateInstructionCallResolver instructionCallResolver =
			new TemplateInstructionCallResolver(declarationScope, runtimeInstructionNormalizer);
	private final Map<ValueDescriptor, GenericModelType> descriptorTypes = new IdentityHashMap<>();
	private final Map<ForEach, GenericModelType> forEachElementTypes = new IdentityHashMap<>();
	private final Map<String, EntityType<? extends InstructionNode>> standardInstructions = new HashMap<>();
	private final TemplateTypeNameResolver typeNameResolver;

	public StandardTemplateApiResolver(ConfigurableTemplateExpertRegistry registry, GenericModelType rootType,
			String rootVariable, CmdResolver cmdResolver) {
		this(registry, rootType, rootVariable, cmdResolver, cmdResolver);
	}

	public StandardTemplateApiResolver(ConfigurableTemplateExpertRegistry registry, GenericModelType rootType,
			String rootVariable, CmdResolver inputCmdResolver, CmdResolver expertCmdResolver) {
		this.registry = registry;
		this.rootType = rootType;
		this.rootVariable = rootVariable;
		this.literalParser = inputCmdResolver == null ? new StandardLiteralParser() : new StandardLiteralParser(inputCmdResolver);
		this.outputParser = new StandardOutputExpressionParser(registry, this);
		this.validationScope = new TemplateValidationScope(Map.of(rootVariable, rootType));
		this.completer = new ExpertModelCompleter(registry);
		this.typeNameResolver = expertCmdResolver == null ? null : new TemplateTypeNameResolver(expertCmdResolver);
		standardInstructions.put("if", If.T);
		standardInstructions.put("for-each", ForEach.T);
		standardInstructions.put("switch", Switch.T);
	}

	@Override
	public Maybe<OutputNode> resolveOutput(String expression, TextRange range) {
		return outputParser.parse(expression, range);
	}

	@Override
	public Maybe<? extends TemplateNode> resolveDirective(char sigil, String invocation, boolean blockFree, TextRange range) {
		if (sigil == '#')
			return resolveDeclaration(invocation);
		if (sigil != '%')
			return Maybe.empty(ParseError.create("Unsupported directive sigil: " + sigil));

		String name = firstToken(invocation);
		String arguments = invocation.substring(name.length()).trim();
		if ("if".equals(name))
			return resolveIf(arguments);
		if ("for-each".equals(name))
			return resolveForEach(arguments);
		if ("switch".equals(name))
			return resolveSwitch(arguments);

		Maybe<List<ParsedArgument>> parsedArguments = parseArguments(arguments);
		if (parsedArguments.isUnsatisfied())
			return Maybe.empty(parsedArguments.whyUnsatisfied());

		Maybe<EntityType<? extends InstructionNode>> type = resolveInstructionType(name);
		if (type.isUnsatisfied())
			return instructionCallResolver.resolve(name, parsedArguments.get());
		return reflectedInstructionNormalizer.normalize(type.get(), parsedArguments.get());
	}

	@Override
	public Reason enterBlock(TemplateNode owner, String blockProperty, TextRange range) {
		validationScope.enter();
		declarationScope.enter();
		if (owner instanceof ForEach forEach && "block".equals(blockProperty)) {
			GenericModelType elementType = forEachElementTypes.get(forEach);
			if (elementType != null) {
				Maybe<GenericModelType> declared = validationScope.declare(forEach.getVariable(), elementType);
				if (declared.isUnsatisfied())
					return declared.whyUnsatisfied();
			}
			if (forEach.getIndexVariable() != null) {
				Maybe<GenericModelType> declared = validationScope.declare(forEach.getIndexVariable(), SimpleTypes.TYPE_INTEGER);
				if (declared.isUnsatisfied())
					return declared.whyUnsatisfied();
			}
		}
		if (owner instanceof DeclareInstruction declaration && "block".equals(blockProperty)) {
			for (Parameter parameter : declaration.getParameters()) {
				GenericModelType type = resolveType(parameter.getType());
				if (type == null)
					return InvalidArgument.create("Unknown parameter type '" + parameter.getType()
							+ "' for instruction " + declaration.getName());
				Maybe<GenericModelType> declared = validationScope.declare(parameter.getName(), type);
				if (declared.isUnsatisfied())
					return declared.whyUnsatisfied();
			}
		}
		return null;
	}

	@Override
	public void exitBlock(TemplateNode owner, String blockProperty) {
		declarationScope.exit();
		validationScope.exit();
	}

	@Override
	public Reason completeAndValidate(ValidationContext context, TemplateNode node, TextRange range) {
		Reason reason = completer.completeAndValidate(this, node);
		if (reason == null && node instanceof DeclareInstruction declaration) {
			Maybe<DeclareInstruction> declared = declarationScope.declare(declaration);
			if (declared.isUnsatisfied())
				return declared.whyUnsatisfied();
		}
		return reason;
	}

	@Override
	public Maybe<ParsedValueExpression> resolveValue(String expression, TextRange range) {
		return resolveValueExpression(expression, false);
	}

	@Override
	public Maybe<ParsedValueExpression> resolveArgumentValue(String expression, TextRange range) {
		return resolveValueExpression(expression, true);
	}

	@Override
	public GenericModelType getType(GenericEntity entity, Property property) {
		ValueDescriptor descriptor = property.getVdDirect(entity);
		if (descriptor != null)
			return descriptorTypes.getOrDefault(descriptor, descriptor.valueType());
		Object direct = property.getDirect(entity);
		ValueDescriptor held = VdHolder.getValueDescriptorIfPossible(direct);
		if (held != null)
			return descriptorTypes.getOrDefault(held, held.valueType());
		return property.getType();
	}

	@Override
	public GenericModelType resolveType(String typeName) {
		return GMF.getTypeReflection().findType(typeName);
	}

	private Maybe<? extends TemplateNode> resolveIf(String arguments) {
		Maybe<ParsedValueExpression> condition = resolveValueExpression(arguments, false);
		if (condition.isUnsatisfied())
			return Maybe.empty(condition.whyUnsatisfied());
		if (!SimpleTypes.TYPE_BOOLEAN.isAssignableFrom(condition.get().type()))
			return Maybe.empty(ParseError.create("If condition must evaluate to boolean"));

		If node = If.T.create();
		set(If.condition.property(), node, condition.get());
		return Maybe.complete(node);
	}

	private Maybe<? extends TemplateNode> resolveSwitch(String arguments) {
		Maybe<ParsedValueExpression> value = resolveValueExpression(arguments, true);
		if (value.isUnsatisfied())
			return Maybe.empty(value.whyUnsatisfied());

		Switch node = Switch.T.create();
		set(Switch.value.property(), node, value.get());
		node.setCases(new ArrayList<>());
		return Maybe.complete(node);
	}

	private Maybe<? extends TemplateNode> resolveForEach(String arguments) {
		List<String> tokens = tokenize(arguments);
		if (tokens.isEmpty())
			return Maybe.empty(ParseError.create("Missing for-each iterable"));

		String iterableExpression = tokens.get(0);
		Maybe<ParsedValueExpression> iterable = resolveValueExpression(iterableExpression, false);
		if (iterable.isUnsatisfied())
			return Maybe.empty(iterable.whyUnsatisfied());
		if (!(iterable.get().type() instanceof CollectionType collectionType))
			return Maybe.empty(ParseError.create("For-each iterable is not a collection: " + iterableExpression));

		ForEach node = ForEach.T.create();
		set(ForEach.iterable.property(), node, iterable.get());
		node.setVariable(option(tokens, "--as", "item"));
		node.setIndexVariable(option(tokens, "--index", null));
		forEachElementTypes.put(node, collectionType.getCollectionElementType());
		return Maybe.complete(node);
	}

	private Maybe<? extends TemplateNode> resolveDeclaration(String invocation) {
		String name = firstToken(invocation);
		if (!"declare-instruction".equals(name))
			return Maybe.empty(ParseError.create("Unsupported declaration: " + name));

		List<String> tokens = tokenize(invocation.substring(name.length()).trim());
		if (tokens.isEmpty())
			return Maybe.empty(ParseError.create("Missing declared instruction name"));

		DeclareInstruction declaration = DeclareInstruction.T.create();
		declaration.setName(tokens.get(0));
		List<Parameter> parameters = new ArrayList<>();
		for (int i = 1; i < tokens.size(); i++) {
			String token = tokens.get(i);
			int colon = token.indexOf(':');
			if (colon <= 0 || colon + 1 >= token.length())
				return Maybe.empty(ParseError.create("Instruction parameter must have the form name:type: " + token));
			Parameter parameter = Parameter.T.create();
			parameter.setName(token.substring(0, colon));
			parameter.setType(token.substring(colon + 1));
			parameters.add(parameter);
		}
		declaration.setParameters(parameters);
		return Maybe.complete(declaration);
	}

	private Maybe<ParsedValueExpression> resolveValueExpression(String expression, boolean allowBareString) {
		String source = expression.trim();
		if (source.isEmpty())
			return Maybe.empty(ParseError.create("Missing expression"));

		Maybe<ParsedLiteral> literal = literalParser.parse(source);
		if (literal.isSatisfied())
			return Maybe.complete(new ParsedValueExpression(literal.get().value(), literal.get().type()));

		Maybe<ParsedValueExpression> descriptor = resolveValueDescriptor(source);
		if (descriptor.isSatisfied())
			return descriptor;

		Maybe<ParsedValueExpression> path = resolvePath(source);
		if (path.isSatisfied())
			return path;
		if (!allowBareString)
			return Maybe.empty(path.whyUnsatisfied());

		return Maybe.complete(new ParsedValueExpression(source, SimpleTypes.TYPE_STRING));
	}

	private Maybe<ParsedValueExpression> resolveValueDescriptor(String source) {
		if (!source.startsWith("(") || !source.endsWith(")"))
			return Maybe.empty(ParseError.create("Not a value descriptor expression: " + source));

		String body = source.substring(1, source.length() - 1).trim();
		if (body.isEmpty())
			return Maybe.empty(ParseError.create("Empty value descriptor expression"));

		String name = firstToken(body);
		String arguments = body.substring(name.length()).trim();
		Maybe<EntityType<? extends ValueDescriptor>> type = resolveValueDescriptorType(name);
		if (type.isUnsatisfied())
			return Maybe.empty(type.whyUnsatisfied());

		Maybe<List<ParsedArgument>> parsedArguments = parseArguments(arguments);
		if (parsedArguments.isUnsatisfied())
			return Maybe.empty(parsedArguments.whyUnsatisfied());

		Maybe<? extends ValueDescriptor> descriptor = reflectedEntityNormalizer.normalize(type.get(), parsedArguments.get());
		if (descriptor.isUnsatisfied())
			return Maybe.empty(descriptor.whyUnsatisfied());

		ValueDescriptor valueDescriptor = descriptor.get();
		descriptorTypes.put(valueDescriptor, valueDescriptor.valueType());
		return Maybe.complete(new ParsedValueExpression(valueDescriptor, valueDescriptor.valueType()));
	}

	private Maybe<ParsedValueExpression> resolvePath(String path) {
		String[] segments = path.trim().split("\\.");
		Maybe<GenericModelType> type = validationScope.resolve(segments[0]);
		if (type.isUnsatisfied())
			return Maybe.empty(type.whyUnsatisfied());

		GenericModelType currentType = type.get();
		for (int i = 1; i < segments.length; i++) {
			if (!(currentType instanceof EntityType<?> entityType))
				return Maybe.empty(ParseError.create("Cannot access property '" + segments[i] + "' on "
						+ currentType.getTypeSignature()));
			Property property = entityType.findProperty(segments[i]);
			if (property == null)
				return Maybe.empty(ParseError.create("Unknown property '" + segments[i] + "' on "
						+ entityType.getTypeSignature()));
			currentType = property.getType();
		}

		Object descriptor = pathDescriptor(path, currentType);
		return Maybe.complete(new ParsedValueExpression(descriptor, currentType));
	}

	private Object pathDescriptor(String path, GenericModelType type) {
		String[] segments = path.trim().split("\\.");
		Variable variable = Variable.T.create();
		variable.setName(segments[0]);
		GenericModelType variableType = validationScope.resolve(segments[0]).get();
		variable.setTypeSignature(variableType.getTypeSignature());
		descriptorTypes.put(variable, variableType);
		if (segments.length == 1)
			return variable;

		PropertyPath propertyPath = PropertyPath.T.create();
		propertyPath.setEntity(variable);
		propertyPath.setPropertyPath(path.substring(segments[0].length() + 1));
		descriptorTypes.put(propertyPath, type);
		return propertyPath;
	}

	@SuppressWarnings("unchecked")
	private Maybe<EntityType<? extends InstructionNode>> resolveInstructionType(String name) {
		EntityType<? extends InstructionNode> standard = standardInstructions.get(name);
		if (standard != null)
			return Maybe.complete(standard);
		if (typeNameResolver == null)
			return Maybe.empty(ParseError.create("Unknown directive: " + name));

		Maybe<EntityType<?>> type = typeNameResolver.resolve(name, TemplateTypeNameResolver.Usage.INSTRUCTION);
		if (type.isUnsatisfied())
			return Maybe.empty(type.whyUnsatisfied());
		return Maybe.complete((EntityType<? extends InstructionNode>) type.get());
	}

	@SuppressWarnings("unchecked")
	private Maybe<EntityType<? extends ValueDescriptor>> resolveValueDescriptorType(String name) {
		List<EntityType<? extends ValueDescriptor>> matches = new ArrayList<>();
		for (EntityType<? extends ValueDescriptor> type : registry.valueDescriptorTypes()) {
			if (name.equals(type.getTypeSignature()) || name.equals(type.getShortName())
					|| name.equals(TemplateTypeNameResolver.toLowerKebabCase(type.getShortName())))
				matches.add(type);
		}
		if (matches.size() == 1)
			return Maybe.complete(matches.get(0));
		if (matches.size() > 1)
			return Maybe.empty(ParseError.create("Ambiguous value descriptor: " + name));

		if (typeNameResolver == null)
			return Maybe.empty(ParseError.create("Unknown value descriptor: " + name));
		Maybe<EntityType<?>> type = typeNameResolver.resolve(name, TemplateTypeNameResolver.Usage.VALUE_DESCRIPTOR);
		if (type.isUnsatisfied())
			return Maybe.empty(type.whyUnsatisfied());
		return Maybe.complete((EntityType<? extends ValueDescriptor>) type.get());
	}

	private Maybe<List<ParsedArgument>> parseArguments(String source) {
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

			Maybe<ParsedValueExpression> value = resolveValueExpression(valueSource, true);
			if (value.isUnsatisfied())
				return Maybe.empty(value.whyUnsatisfied());
			arguments.add(new ParsedArgument(name, value.get().value(), value.get().type()));
		}
		return Maybe.complete(arguments);
	}

	private static void set(Property property, GenericEntity entity, ParsedValueExpression value) {
		if (value.value() instanceof ValueDescriptor descriptor)
			property.setVdDirect(entity, descriptor);
		else
			property.setDirect(entity, value.value());
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
		int nesting = 0;
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
			} else if (ch == '(' || ch == '[' || ch == '{') {
				nesting++;
			} else if (ch == ')' || ch == ']' || ch == '}') {
				nesting = Math.max(0, nesting - 1);
			} else if (Character.isWhitespace(ch) && nesting == 0) {
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

	private static String option(List<String> tokens, String option, String defaultValue) {
		for (int i = 1; i + 1 < tokens.size(); i++)
			if (option.equals(tokens.get(i)))
				return tokens.get(i + 1);
		return defaultValue;
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
}
