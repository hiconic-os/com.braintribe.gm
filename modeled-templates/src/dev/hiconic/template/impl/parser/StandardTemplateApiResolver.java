package dev.hiconic.template.impl.parser;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.collection.PlainList;
import com.braintribe.model.generic.collection.PlainMap;
import com.braintribe.model.generic.collection.PlainSet;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.meta.cmd.CmdResolver;

import dev.hiconic.template.api.TemplateExpertRegistry;
import dev.hiconic.template.api.Template;
import dev.hiconic.template.api.TemplateParserResolver;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.impl.ConfigurableTemplateExpertRegistry;
import dev.hiconic.template.impl.node.DeclareInstructionEvaluator;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.SourceText;
import dev.hiconic.template.model.core.TypeReference;
import dev.hiconic.template.model.core.vd.TemplateVariable;
import dev.hiconic.template.model.core.vd.TemplatePropertyPath;
import dev.hiconic.template.model.core.path.PathAccess;
import dev.hiconic.template.model.core.path.PropertyAccess;
import dev.hiconic.template.model.core.path.PropertyReference;
import dev.hiconic.template.model.core.path.ListIndexAccess;
import dev.hiconic.template.model.core.path.MapKeyAccess;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.decl.RuntimePropertySpecification;
import dev.hiconic.template.model.core.decl.RuntimeTypeSpecification;
import dev.hiconic.template.model.core.decl.VariableDefinition;
import dev.hiconic.template.model.core.decl.Var;
import dev.hiconic.template.model.core.instr.ForEach;
import dev.hiconic.template.model.core.instr.ForEachEntry;
import dev.hiconic.template.model.core.instr.If;
import dev.hiconic.template.model.core.instr.InstructionNode;
import dev.hiconic.template.model.core.instr.BlockNode;
import dev.hiconic.template.model.core.instr.BreakableNode;
import dev.hiconic.template.model.core.instr.ContinuableNode;
import dev.hiconic.template.model.core.instr.VariableDefiningNode;
import dev.hiconic.template.model.core.instr.DirectiveNode;
import dev.hiconic.template.model.core.instr.Switch;
import dev.hiconic.template.model.core.instr.Set;
import dev.hiconic.template.model.core.instr.Append;
import dev.hiconic.template.model.core.instr.Insert;
import dev.hiconic.template.model.core.instr.Add;
import dev.hiconic.template.model.core.instr.Put;
import dev.hiconic.template.model.core.instr.Remove;
import dev.hiconic.template.model.core.instr.RemoveAt;
import dev.hiconic.template.model.core.instr.Break;
import dev.hiconic.template.model.core.instr.BlockClause;
import dev.hiconic.template.model.core.instr.Case;
import dev.hiconic.template.model.core.instr.Continue;
import dev.hiconic.template.model.core.instr.Default;
import dev.hiconic.template.model.core.instr.While;
import dev.hiconic.template.model.core.instr.Repeat;
import dev.hiconic.template.model.core.instr.RenderTemplate;
import dev.hiconic.template.model.core.instr.When;
import dev.hiconic.template.model.core.instr.AssignmentTarget;
import dev.hiconic.template.model.core.instr.PropertyAssignmentTarget;
import dev.hiconic.template.model.core.instr.VariableAssignmentTarget;
import dev.hiconic.template.model.core.instr.InvokeInstruction;
import dev.hiconic.template.model.core.instr.WhitespacePolicy;
import dev.hiconic.template.model.parse.TextRange;

public class StandardTemplateApiResolver implements TemplateParserResolver, TemplateValueExpressionResolver, ValidationContext {
	private final TemplateExpertRegistry registry;
	private final GenericModelType rootType;
	private final String rootVariable;
	private final Map<String, Template<?>> templates;
	private final StandardLiteralParser literalParser;
	private final StandardOutputExpressionParser outputParser;
	private final TemplateValidationScope validationScope;
	private final ExpertModelCompleter completer;
	private final MetadataConstraintValidator constraintValidator;
	private final ReflectedEntityNormalizer reflectedEntityNormalizer =
			new ReflectedEntityNormalizer(this::resolveExpectedValue, this::recordArgumentType, this::expectedArgumentType);
	private final ReflectedInstructionNormalizer reflectedInstructionNormalizer =
			new ReflectedInstructionNormalizer(this::resolveExpectedValue, this::recordArgumentType, this::expectedArgumentType);
	private final TemplateDeclarationScope declarationScope = new TemplateDeclarationScope();
	private final TemplateSymbolTable symbols = new TemplateSymbolTable();
	private final RuntimeInstructionNormalizer runtimeInstructionNormalizer = new RuntimeInstructionNormalizer(this, this::resolveExpectedValue);
	private final TemplateInstructionCallResolver instructionCallResolver =
			new TemplateInstructionCallResolver(declarationScope, runtimeInstructionNormalizer);
	private final Map<ValueDescriptor, GenericModelType> descriptorTypes = new IdentityHashMap<>();
	private final Map<GenericEntity, Map<Property, GenericModelType>> argumentTypes = new IdentityHashMap<>();
	private final Map<GenericEntity, Map<Property, TextRange>> argumentRanges = new IdentityHashMap<>();
	private final Map<GenericEntity, java.util.Set<Property>> nullArguments = new IdentityHashMap<>();
	private final Map<String, EntityType<? extends InstructionNode>> standardInstructions = new HashMap<>();
	private final Deque<Boolean> breakableScopes = new ArrayDeque<>();
	private final Deque<Boolean> continuableScopes = new ArrayDeque<>();
	private final TemplateTypeNameResolver inputTypeNameResolver;
	private final TemplateTypeNameResolver expertTypeNameResolver;
	private TextRange activeRange;

	public StandardTemplateApiResolver(ConfigurableTemplateExpertRegistry registry, GenericModelType rootType,
			String rootVariable, CmdResolver cmdResolver) {
		this(registry, rootType, rootVariable, cmdResolver, cmdResolver);
	}

	public StandardTemplateApiResolver(ConfigurableTemplateExpertRegistry registry, GenericModelType rootType,
			String rootVariable, CmdResolver inputCmdResolver, CmdResolver expertCmdResolver) {
		this(registry, rootType, rootVariable, inputCmdResolver, expertCmdResolver, Map.of());
	}

	public StandardTemplateApiResolver(ConfigurableTemplateExpertRegistry registry, GenericModelType rootType,
			String rootVariable, CmdResolver inputCmdResolver, CmdResolver expertCmdResolver,
			Map<String, Template<?>> templates) {
		this.registry = registry;
		this.rootType = rootType;
		this.rootVariable = rootVariable;
		this.templates = Map.copyOf(templates);
		this.literalParser = inputCmdResolver == null ? new StandardLiteralParser() : new StandardLiteralParser(inputCmdResolver);
		this.outputParser = new StandardOutputExpressionParser(registry, this);
		this.validationScope = new TemplateValidationScope(Map.of(rootVariable, rootType));
		this.completer = new ExpertModelCompleter(registry);
		this.constraintValidator = new MetadataConstraintValidator(inputCmdResolver, expertCmdResolver);
		this.inputTypeNameResolver = inputCmdResolver == null ? null : new TemplateTypeNameResolver(inputCmdResolver);
		this.expertTypeNameResolver = expertCmdResolver == null ? null : new TemplateTypeNameResolver(expertCmdResolver);
		standardInstructions.put("if", If.T);
		standardInstructions.put("for-each", ForEach.T);
		standardInstructions.put("for-each-entry", ForEachEntry.T);
		standardInstructions.put("while", While.T);
		standardInstructions.put("repeat", Repeat.T);
		standardInstructions.put("render-template", RenderTemplate.T);
		standardInstructions.put("switch", Switch.T);
		standardInstructions.put("set", Set.T);
		standardInstructions.put("append", Append.T);
		standardInstructions.put("insert", Insert.T);
		standardInstructions.put("add", Add.T);
		standardInstructions.put("put", Put.T);
		standardInstructions.put("remove", Remove.T);
		standardInstructions.put("remove-at", RemoveAt.T);
		standardInstructions.put("break", Break.T);
		standardInstructions.put("continue", Continue.T);
		standardInstructions.put("var", Var.T);
		if (registry.findScalarParser(Symbol.T) == null)
			registry.registerScalarParser(Symbol.T, (source, context) -> source.matches("[A-Za-z_][A-Za-z0-9_-]*")
					? Maybe.complete(symbols.intern(source)) : Maybe.empty(ParseError.create("Invalid symbol: " + source)));
		if (registry.findScalarParser(TypeReference.T) == null)
			registry.registerScalarParser(TypeReference.T, (source, context) -> {
				String signature = normalizeTypeSignature(source);
				try {
					GenericModelType resolved = context.resolveType(signature);
					return resolved == null ? Maybe.empty(ParseError.create("Unknown type reference: " + source))
							: Maybe.complete(DefinitionTools.type(resolved.getTypeSignature()));
				} catch (RuntimeException e) {
					return Maybe.empty(ParseError.create("Invalid type reference '" + source + "': " + e.getMessage()));
				}
			});
		if (registry.findScalarParser(VariableDefinition.T) == null)
			registry.registerScalarParser(VariableDefinition.T, (source, context) -> parseVariableDefinition(source));
		if (registry.findScalarParser(AssignmentTarget.T) == null)
			registry.registerScalarParser(AssignmentTarget.T,
					(dev.hiconic.template.api.ScalarEntityParser<AssignmentTarget>) (source, context) -> resolveAssignmentTarget(source));
		declareTemplateDelegates();
	}

	@Override
	public void beginParse() {
		constraintValidator.reset();
		descriptorTypes.clear();
		argumentTypes.clear();
		argumentRanges.clear();
		nullArguments.clear();
	}

	private void declareTemplateDelegates() {
		for (Map.Entry<String, Template<?>> entry : templates.entrySet()) {
			DeclareInstruction declaration = delegateDeclaration(entry.getKey(), entry.getValue());
			Maybe<DeclareInstruction> declared = declarationScope.declare(declaration);
			if (declared.isUnsatisfied())
				throw new IllegalArgumentException(declared.whyUnsatisfied().stringify());
		}
	}

	private DeclareInstruction delegateDeclaration(String name, Template<?> template) {
		VariableDefinition parameter = DefinitionTools.variable("input", template.rootType().getTypeSignature(), false);
		parameter.setSymbol(symbols.intern("input"));

		RuntimePropertySpecification property = RuntimePropertySpecification.T.create();
		property.setName("input");
		property.setTypeSignature(template.rootType().getTypeSignature());
		property.setPositionalIndex(0);
		property.setRequired(true);
		property.setMetaData(new ArrayList<>());

		RuntimeTypeSpecification argumentType = RuntimeTypeSpecification.T.create();
		argumentType.setName(name + "Arguments");
		argumentType.setProperties(new ArrayList<>());
		argumentType.getProperties().add(property);

		TemplateVariable input = TemplateVariable.T.create();
		input.setName("input");
		input.setSymbol(parameter.getSymbol());
		input.setTypeSignature(template.rootType().getTypeSignature());
		descriptorTypes.put(input, template.rootType());

		RenderTemplate render = RenderTemplate.T.create();
		render.setName(name);
		RenderTemplate.input.property().setVdDirect(render, input);
		render.setInputType(DefinitionTools.type(template.rootType().getTypeSignature()));

		DeclareInstruction declaration = DeclareInstruction.T.create();
		declaration.setName(symbols.intern(name));
		declaration.setParameters(new ArrayList<>());
		declaration.getParameters().add(parameter);
		declaration.setVariableDefinitions(declaration.getParameters());
		declaration.setArgumentType(argumentType);
		declaration.setBlock(render);
		return declaration;
	}

	@Override
	public Maybe<OutputNode> resolveOutput(String expression, TextRange range) {
		Located located = trim(expression, range);
		return withinRange(located.range(), () -> outputParser.parse(located.text(), located.range()));
	}

	@Override
	public Maybe<? extends TemplateNode> resolveDirective(char sigil, String invocation, boolean blockFree, TextRange range) {
		Located located = trim(invocation, range);
		return withinRange(located.range(), () -> resolveDirectiveInternal(sigil, located.text(), blockFree));
	}

	@Override
	public java.util.Set<String> clauseMarkers(GenericModelType expectedClauseType) {
		if (!(expectedClauseType instanceof EntityType<?> expected))
			return java.util.Set.of();
		java.util.Set<String> markers = new LinkedHashSet<>();
		addStandardClauseMarker(markers, expected, Case.T);
		addStandardClauseMarker(markers, expected, When.T);
		addStandardClauseMarker(markers, expected, Default.T);
		if (expertTypeNameResolver != null)
			markers.addAll(expertTypeNameResolver.aliasesForAssignableClauses(expected));
		return markers;
	}

	private static void addStandardClauseMarker(java.util.Set<String> markers, EntityType<?> expected,
			EntityType<? extends BlockClause> type) {
		if (expected.isAssignableFrom(type))
			markers.add(TemplateTypeNameResolver.toLowerKebabCase(type.getShortName()));
	}

	@Override
	public Maybe<? extends GenericEntity> resolveClause(GenericModelType expectedClauseType, String marker,
			String invocation, TextRange range) {
		if (!(expectedClauseType instanceof EntityType<?> expected))
			return Maybe.empty(ParseError.create("Clause target is not an entity type: "
					+ expectedClauseType.getTypeSignature()));
		Maybe<? extends EntityType<?>> type = resolveClauseType(expected, marker);
		if (type.isUnsatisfied())
			return Maybe.empty(type.whyUnsatisfied());
		Maybe<List<ParsedArgument>> arguments = parseArguments(invocation, range, type.get());
		return arguments.isSatisfied()
				? reflectedEntityNormalizer.normalize(type.get(), arguments.get())
				: Maybe.empty(arguments.whyUnsatisfied());
	}

	private Maybe<? extends EntityType<?>> resolveClauseType(EntityType<?> expected, String marker) {
		for (EntityType<? extends BlockClause> type : List.of(Case.T, When.T, Default.T))
			if (expected.isAssignableFrom(type)
					&& marker.equals(TemplateTypeNameResolver.toLowerKebabCase(type.getShortName())))
				return Maybe.complete(type);
		if (expertTypeNameResolver == null)
			return Maybe.empty(ParseError.create("Unknown block clause: " + marker));
		Maybe<EntityType<?>> resolved = expertTypeNameResolver.resolve(marker, TemplateTypeNameResolver.Usage.CLAUSE);
		if (resolved.isUnsatisfied())
			return Maybe.empty(resolved.whyUnsatisfied());
		return expected.isAssignableFrom(resolved.get())
				? resolved
				: Maybe.empty(ParseError.create("Clause '" + marker + "' is not assignable to "
						+ expected.getTypeSignature()));
	}

	private Maybe<? extends TemplateNode> resolveDirectiveInternal(char sigil, String invocation, boolean blockFree) {
		if (sigil != '%')
			return Maybe.empty(ParseError.create("Unsupported directive sigil: " + sigil));

		String name = firstToken(invocation);
		int argumentsStart = skipWhitespace(invocation, name.length());
		String arguments = invocation.substring(argumentsStart);
		TextRange argumentsRange = SourceRanges.subRange(activeRange, invocation, argumentsStart, invocation.length());
		if ("declare-instruction".equals(name))
			return resolveDeclaration(invocation);

		Maybe<List<ParsedArgument>> parsedArguments = parseArguments(arguments, argumentsRange);
		if (parsedArguments.isUnsatisfied())
			return Maybe.empty(parsedArguments.whyUnsatisfied());

		Maybe<EntityType<? extends DirectiveNode>> type = resolveDirectiveType(name);
		if (type.isUnsatisfied()) {
			List<ParsedArgument> invocationArguments = new ArrayList<>();
			ParsedArgument whitespace = null;
			for (ParsedArgument argument : parsedArguments.get()) {
				if ("whitespace".equals(argument.name())) whitespace = argument; else invocationArguments.add(argument);
			}
			Maybe<InvokeInstruction> resolvedInvocation = instructionCallResolver.resolve(name, invocationArguments);
			if (resolvedInvocation.isUnsatisfied() || whitespace == null) return resolvedInvocation;
			Maybe<ParsedValueExpression> policy = resolveExpectedValue(whitespace.source(), WhitespacePolicy.T, whitespace.range());
			if (policy.isUnsatisfied()) return Maybe.empty(policy.whyUnsatisfied());
			resolvedInvocation.get().setWhitespace((WhitespacePolicy) policy.get().value());
			return resolvedInvocation;
		}
		Maybe<List<ParsedArgument>> typedArguments = parseArguments(arguments, argumentsRange, type.get());
		return typedArguments.isSatisfied()
				? reflectedInstructionNormalizer.normalize(type.get(), typedArguments.get())
				: Maybe.empty(typedArguments.whyUnsatisfied());
	}

	@Override
	public Reason predeclareDirective(char sigil, String invocation, TextRange range) {
		Located located = trim(invocation, range);
		if (sigil != '%' || !"declare-instruction".equals(firstToken(located.text())))
			return null;
		Maybe<DeclareInstruction> parsed = withinRange(located.range(), () -> parseDeclaration(located.text()));
		if (parsed.isUnsatisfied())
			return parsed.whyUnsatisfied();
		Reason completion = new DeclareInstructionEvaluator().completeSignature(this, parsed.get());
		if (completion != null)
			return completion;
		Maybe<DeclareInstruction> declared = declarationScope.declare(parsed.get());
		return declared.isUnsatisfied() ? declared.whyUnsatisfied() : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Reason completeScope(TemplateNode owner, TextRange range) {
		var evaluator = (dev.hiconic.template.api.TemplateNodeEvaluator<TemplateNode>) registry.findEvaluator(owner.entityType());
		return evaluator == null ? null : evaluator.completeScope(this, owner);
	}

	@Override
	public Reason enterBlock(TemplateNode owner, String blockProperty, TextRange range) {
		validationScope.enter();
		declarationScope.enter();
		boolean primaryBlock = "block".equals(blockProperty);
		breakableScopes.push(primaryBlock && owner instanceof BreakableNode);
		continuableScopes.push(primaryBlock && owner instanceof ContinuableNode);
		if ("block".equals(blockProperty) && owner instanceof BlockNode block
				&& block.getVariableDefinitions() != null) {
			for (VariableDefinition definition : block.getVariableDefinitions()) {
				GenericModelType definitionType = resolveType(DefinitionTools.type(definition));
				Maybe<GenericModelType> declared = validationScope.declare(DefinitionTools.name(definition), definitionType);
				if (declared.isUnsatisfied())
					return declared.whyUnsatisfied();
			}
		}
		return null;
	}

	@Override
	public void exitBlock(TemplateNode owner, String blockProperty) {
		continuableScopes.pop();
		breakableScopes.pop();
		declarationScope.exit();
		validationScope.exit();
	}

	@Override
	public Reason completeAndValidate(ValidationContext context, TemplateNode node, TextRange range) {
		Reason reason = completer.completeAndValidate(this, node);
		if (reason == null) reason = constraintValidator.validate(this, node, range);
		if (reason == null && node instanceof VariableDefiningNode defining && !(node instanceof BlockNode)
				&& defining.getVariableDefinitions() != null) {
			for (VariableDefinition definition : defining.getVariableDefinitions()) {
				GenericModelType type = resolveType(DefinitionTools.type(definition));
				if (type == null)
					return InvalidArgument.create("Unknown variable type '" + DefinitionTools.type(definition) + "'");
				Maybe<GenericModelType> declared = definition.getMutable()
						? validationScope.declareMutable(DefinitionTools.name(definition), type)
						: validationScope.declare(DefinitionTools.name(definition), type);
				if (declared.isUnsatisfied()) return declared.whyUnsatisfied();
			}
		}
		return reason;
	}

	@Override
	public Maybe<ParsedValueExpression> resolveValue(String expression, TextRange range) {
		Located located = trim(expression, range);
		return withinRange(located.range(), () -> resolveValueExpression(located.text(), false));
	}

	@Override
	public Maybe<ParsedValueExpression> resolveArgumentValue(String expression, TextRange range) {
		Located located = trim(expression, range);
		return withinRange(located.range(), () -> resolveValueExpression(located.text(), true));
	}

	@Override
	public Maybe<ParsedValueExpression> resolveArgumentValue(String expression, GenericModelType expectedType, TextRange range) {
		Located located = trim(expression, range);
		return withinRange(located.range(), () -> resolveValueExpression(located.text(), true, expectedType));
	}

	private <T> T withinRange(TextRange range, java.util.function.Supplier<T> action) {
		TextRange previous = activeRange;
		activeRange = range;
		try {
			return action.get();
		} finally {
			activeRange = previous;
		}
	}

	private static Located trim(String source, TextRange range) {
		int start = 0;
		int end = source.length();
		while (start < end && Character.isWhitespace(source.charAt(start))) start++;
		while (end > start && Character.isWhitespace(source.charAt(end - 1))) end--;
		return new Located(source.substring(start, end), SourceRanges.subRange(range, source, start, end));
	}

	private static int skipWhitespace(String source, int start) {
		while (start < source.length() && Character.isWhitespace(source.charAt(start))) start++;
		return start;
	}

	private record Located(String text, TextRange range) {
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
		Map<Property, GenericModelType> entityTypes = argumentTypes.get(entity);
		return entityTypes == null ? property.getType() : entityTypes.getOrDefault(property, property.getType());
	}

	@SuppressWarnings("unchecked")
	private GenericModelType expectedArgumentType(GenericEntity entity, Property property, GenericModelType reflectedType) {
		if (entity instanceof When && property == When.condition.property())
			return SimpleTypes.TYPE_BOOLEAN;
		if (entity instanceof VariableDefinition definition && property == VariableDefinition.defaultValue.property()
				&& definition.getType() != null)
			return resolveType(definition.getType().getTypeSignature());
		if (entity instanceof TemplateNode node) {
			var evaluator = (dev.hiconic.template.api.TemplateNodeEvaluator<TemplateNode>) registry.findEvaluator(node.entityType());
			if (evaluator != null) {
				GenericModelType expected = evaluator.expectedArgumentType(this, node, property);
				if (expected != null) return expected;
			}
		}
		if (entity instanceof ValueDescriptor descriptor) {
			@SuppressWarnings("unchecked")
			VdEvaluator<ValueDescriptor, Object> evaluator =
					(VdEvaluator<ValueDescriptor, Object>) registry.findVdEvaluator(descriptor.entityType());
			if (evaluator != null) {
				GenericModelType expected = evaluator.expectedArgumentType(this, descriptor, property);
				if (expected != null) return expected;
			}
		}
		return reflectedType;
	}

	@Override
	public boolean isExplicitNull(GenericEntity entity, Property property) {
		java.util.Set<Property> properties = nullArguments.get(entity);
		return properties != null && properties.contains(property);
	}

	private void recordArgumentType(GenericEntity entity, Property property, GenericModelType type, TextRange range) {
		argumentTypes.computeIfAbsent(entity, ignored -> new IdentityHashMap<>()).put(property, type);
		if (range != null)
			argumentRanges.computeIfAbsent(entity, ignored -> new IdentityHashMap<>()).put(property, range);
		if (property.getDirect(entity) == null)
			nullArguments.computeIfAbsent(entity, ignored -> java.util.Collections.newSetFromMap(new IdentityHashMap<>()))
					.add(property);
	}

	@Override
	public TextRange getRange(GenericEntity entity, Property property) {
		Map<Property, TextRange> ranges = argumentRanges.get(entity);
		return ranges == null ? null : ranges.get(property);
	}

	@Override
	public boolean canBreak() {
		for (Boolean breakable : breakableScopes)
			if (breakable) return true;
		return false;
	}

	@Override
	public boolean canContinue() {
		for (Boolean continuable : continuableScopes)
			if (continuable) return true;
		return false;
	}

	@Override
	public GenericModelType templateRootType(String name) {
		Template<?> template = templates.get(name);
		return template == null ? null : template.rootType();
	}

	@Override
	public GenericModelType resolveType(String typeName) {
		String normalized = normalizeTypeSignature(typeName);
		GenericModelType reflected = GMF.getTypeReflection().findType(normalized);
		if (reflected != null)
			return reflected;
		GenericModelType resolved = resolveEntityType(inputTypeNameResolver, normalized);
		if (resolved != null)
			return resolved;
		resolved = resolveEntityType(expertTypeNameResolver, normalized);
		if (resolved != null)
			return resolved;
		return null;
	}

	private static GenericModelType resolveEntityType(TemplateTypeNameResolver resolver, String typeName) {
		if (resolver == null)
			return null;
		Maybe<EntityType<?>> resolved = resolver.resolve(typeName, TemplateTypeNameResolver.Usage.ENTITY);
		return resolved.isSatisfied() ? resolved.get() : null;
	}

	private Maybe<AssignmentTarget> resolveAssignmentTarget(String source) {
		Maybe<ParsedValueExpression> parsedPath = resolvePath(source);
		if (parsedPath.isUnsatisfied()) return Maybe.empty(parsedPath.whyUnsatisfied());
		if (parsedPath.get().value() instanceof TemplateVariable variable) {
			Maybe<TemplateValidationScope.Binding> binding = validationScope.resolveBinding(variable.getName());
			if (binding.isUnsatisfied()) return Maybe.empty(binding.whyUnsatisfied());
			if (!binding.get().mutable())
				return Maybe.empty(ParseError.create("Assignment target is rooted in readonly variable: " + variable.getName()));
			VariableAssignmentTarget target = VariableAssignmentTarget.T.create();
			target.setSymbol(variable.getSymbol());
			target.setNullable(true);
			target.setTypeSignature(parsedPath.get().type().getTypeSignature());
			return Maybe.complete(target);
		}
		if (!(parsedPath.get().value() instanceof TemplatePropertyPath path))
			return Maybe.empty(ParseError.create("Invalid assignment target: " + source));
		TemplateVariable root = (TemplateVariable) path.getRoot();
		Maybe<TemplateValidationScope.Binding> binding = validationScope.resolveBinding(root.getName());
		if (binding.isUnsatisfied()) return Maybe.empty(binding.whyUnsatisfied());
		if (!binding.get().mutable())
			return Maybe.empty(ParseError.create("Assignment target is rooted in readonly variable: " + root.getName()));
		for (PathAccess access : path.getAccesses())
			if (access instanceof PropertyAccess property && property.getOptional()
					|| access instanceof ListIndexAccess index && index.getOptional()
					|| access instanceof MapKeyAccess key && key.getOptional())
				return Maybe.empty(ParseError.create("Optional navigation is not allowed in assignment targets: " + source));
		PathAccess last = path.getAccesses().get(path.getAccesses().size() - 1);
		PropertyAssignmentTarget target = PropertyAssignmentTarget.T.create();
		target.setPath(path);
		target.setNullable(last instanceof PropertyAccess property
				? property.getProperty().getResolvedProperty().isNullable() : true);
		target.setTypeSignature(parsedPath.get().type().getTypeSignature());
		return Maybe.complete(target);
	}

	private Maybe<? extends TemplateNode> resolveDeclaration(String invocation) {
		Maybe<DeclareInstruction> parsed = parseDeclaration(invocation);
		if (parsed.isUnsatisfied())
			return parsed;
		DeclareInstruction declaration = declarationScope.resolveLocal(parsed.get().getName().getName());
		return declaration == null
				? Maybe.empty(ParseError.create("Instruction was not predeclared: " + parsed.get().getName()))
				: Maybe.complete(declaration);
	}

	private Maybe<DeclareInstruction> parseDeclaration(String invocation) {
		String name = firstToken(invocation);
		if (!"declare-instruction".equals(name))
			return Maybe.empty(ParseError.create("Unsupported declaration: " + name));
		int argumentsStart = skipWhitespace(invocation, name.length());
		String argumentSource = invocation.substring(argumentsStart);
		Maybe<List<ParsedArgument>> arguments = parseArguments(argumentSource,
				SourceRanges.subRange(activeRange, invocation, argumentsStart, invocation.length()), DeclareInstruction.T);
		if (arguments.isUnsatisfied()) return Maybe.empty(arguments.whyUnsatisfied());
		Maybe<DeclareInstruction> normalized = reflectedEntityNormalizer.normalize(DeclareInstruction.T, arguments.get());
		if (normalized.isUnsatisfied()) return normalized;
		DeclareInstruction declaration = normalized.get();
		if (declaration.getParameters() == null) declaration.setParameters(new ArrayList<>());
		for (VariableDefinition parameter : declaration.getParameters())
			parameter.setSymbol(symbols.intern(parameter.getSymbol().getName()));
		declaration.setVariableDefinitions(declaration.getParameters());
		return Maybe.complete(declaration);
	}

	private Maybe<VariableDefinition> parseVariableDefinition(String source) {
		String body = source;
		if (source.startsWith("{") && source.endsWith("}")) body = source.substring(1, source.length() - 1).trim();
		else if (source.startsWith("(") && source.endsWith(")")) {
			List<String> explicit = tokenize(source.substring(1, source.length() - 1).trim());
			if (explicit.isEmpty() || !("par".equals(explicit.get(0)) || "variable-definition".equals(explicit.get(0))))
				return Maybe.empty(ParseError.create("Expected variable-definition type in " + source));
			body = String.join(" ", explicit.subList(1, explicit.size()));
		}
		List<String> parts = tokenize(body);
		if (parts.size() < 1 || parts.size() > 2)
			return Maybe.empty(ParseError.create("Variable definition expects a symbol and optional type: " + source));
		return Maybe.complete(DefinitionTools.variable(parts.get(0), parts.size() == 2 ? parts.get(1) : null, false));
	}

	private Maybe<ParsedValueExpression> resolveValueExpression(String expression, boolean allowBareString) {
		return resolveValueExpression(expression, allowBareString, null);
	}

	private Maybe<ParsedValueExpression> resolveExpectedValue(String expression, GenericModelType expectedType, TextRange range) {
		Located located = trim(expression, range);
		return withinRange(located.range(), () -> resolveValueExpression(located.text(), true, expectedType));
	}

	private Maybe<ParsedValueExpression> resolveValueExpression(String expression, boolean allowBareString,
			GenericModelType expectedType) {
		String source = expression.trim();
		if (source.isEmpty())
			return Maybe.empty(ParseError.create("Missing expression"));
		List<Token> pipeline = splitPipeline(source);
		if (pipeline.size() > 1)
			return resolvePipeline(source, pipeline, allowBareString, expectedType);
		if (expectedType instanceof EntityType<?> entityType && !source.startsWith("{") && !source.startsWith("(")) {
			var scalarParser = registry.findScalarParser(entityType);
			if (scalarParser != null) {
				@SuppressWarnings("unchecked")
				Maybe<? extends GenericEntity> scalar = ((dev.hiconic.template.api.ScalarEntityParser<GenericEntity>) scalarParser).parse(source, this);
				return scalar.isSatisfied() ? Maybe.complete(new ParsedValueExpression(scalar.get(), entityType))
						: Maybe.empty(scalar.whyUnsatisfied());
			}
		}
		if (expectedType == Symbol.T)
			return source.matches("[A-Za-z_][A-Za-z0-9_-]*")
					? Maybe.complete(new ParsedValueExpression(symbols.intern(source), Symbol.T))
					: Maybe.empty(ParseError.create("Invalid symbol: " + source));
		if (expectedType == TypeReference.T && !source.startsWith("{") && !source.startsWith("(")) {
			GenericModelType resolved = resolveType(source);
			return resolved == null ? Maybe.empty(ParseError.create("Unknown type reference: " + source))
					: Maybe.complete(new ParsedValueExpression(DefinitionTools.type(resolved.getTypeSignature()), TypeReference.T));
		}
		if (expectedType == VariableDefinition.T && !source.startsWith("{") && !source.startsWith("(")) {
			Maybe<VariableDefinition> definition = parseVariableDefinition(source);
			return definition.isSatisfied() ? Maybe.complete(new ParsedValueExpression(definition.get(), VariableDefinition.T))
					: Maybe.empty(definition.whyUnsatisfied());
		}

		Maybe<ParsedLiteral> literal = literalParser.parse(source, expectedType);
		if (literal.isSatisfied())
			return Maybe.complete(new ParsedValueExpression(literal.get().value(), literal.get().type()));

		if (isCollectionLiteralSyntax(source, expectedType))
			return resolveCollectionLiteral(source, expectedType);
		if (source.startsWith("("))
			return resolveEntityExpression(source, expectedType);

		Maybe<ParsedValueExpression> inferred = resolveStructuredLiteral(source, expectedType);
		if (inferred.isSatisfied())
			return inferred;

		Maybe<ParsedValueExpression> path = resolvePath(source);
		if (path.isSatisfied())
			return path;
		if (!allowBareString)
			return Maybe.empty(path.whyUnsatisfied());

		return Maybe.complete(new ParsedValueExpression(source, SimpleTypes.TYPE_STRING));
	}

	private Maybe<ParsedValueExpression> resolvePipeline(String source, List<Token> stages, boolean allowBareString,
			GenericModelType expectedType) {
		Token first = stages.get(0);
		TextRange firstRange = SourceRanges.subRange(activeRange, source, first.start(), first.end());
		Maybe<ParsedValueExpression> current = withinRange(firstRange,
				() -> resolveValueExpression(first.text(), allowBareString, null));
		if (current.isUnsatisfied()) return current;
		current = Maybe.complete(new ParsedValueExpression(current.get().value(), current.get().type(), firstRange));
		for (int i = 1; i < stages.size(); i++) {
			Token stageToken = stages.get(i);
			String stage = stageToken.text();
			TextRange stageRange = SourceRanges.subRange(activeRange, source, stageToken.start(), stageToken.end());
			if (!stage.startsWith("(") || !stage.endsWith(")"))
				return Maybe.empty(ParseError.create("Pipeline stage must be an explicit ValueDescriptor entity: " + stage));
			Located body = trim(stage.substring(1, stage.length() - 1),
					SourceRanges.subRange(stageRange, stage, 1, stage.length() - 1));
			String name = firstToken(body.text());
			if (name.isEmpty()) return Maybe.empty(ParseError.create("Empty pipeline stage"));
			Maybe<EntityType<? extends ValueDescriptor>> type = resolveValueDescriptorType(name);
			if (type.isUnsatisfied()) return Maybe.empty(type.whyUnsatisfied());
			int argumentsStart = skipWhitespace(body.text(), name.length());
			String argumentsSource = body.text().substring(argumentsStart);
			Maybe<List<ParsedArgument>> arguments = parseArguments(argumentsSource,
					SourceRanges.subRange(body.range(), body.text(), argumentsStart, body.text().length()), type.get());
			if (arguments.isUnsatisfied()) return Maybe.empty(arguments.whyUnsatisfied());
			Maybe<? extends ValueDescriptor> descriptor = reflectedEntityNormalizer.normalize(type.get(), current.get(), arguments.get());
			if (descriptor.isUnsatisfied()) return Maybe.empty(descriptor.whyUnsatisfied());
			ValueDescriptor value = descriptor.get();
			@SuppressWarnings("unchecked")
			VdEvaluator<ValueDescriptor, Object> evaluator =
					(VdEvaluator<ValueDescriptor, Object>) registry.findVdEvaluator(value.entityType());
			if (evaluator == null)
				return Maybe.empty(ParseError.create("No ValueDescriptor expert registered for pipeline stage " + name));
			Reason completion = evaluator.complete(this, value);
			if (completion != null) return Maybe.empty(completion);
			GenericModelType valueType = value.valueType();
			descriptorTypes.put(value, valueType);
			current = Maybe.complete(new ParsedValueExpression(value, valueType, stageRange));
		}
		if (expectedType != null && (current.get().type() == null || !expectedType.isAssignableFrom(current.get().type())))
			return Maybe.empty(ParseError.create("Pipeline result is not assignable to " + expectedType.getTypeSignature()));
		return current;
	}

	private static List<Token> splitPipeline(String source) {
		List<Token> stages = new ArrayList<>();
		int start = 0;
		int parentheses = 0;
		int brackets = 0;
		int braces = 0;
		char quote = 0;
		boolean found = false;
		for (int i = 0; i < source.length(); i++) {
			char ch = source.charAt(i);
			if (quote != 0) {
				if (ch == quote && (i == 0 || source.charAt(i - 1) != '\\')) quote = 0;
				continue;
			}
			if (ch == '\'' || ch == '"') { quote = ch; continue; }
			if (ch == '(') parentheses++;
			else if (ch == ')') parentheses--;
			else if (ch == '[') brackets++;
			else if (ch == ']') brackets--;
			else if (ch == '{') braces++;
			else if (ch == '}') braces--;
			else if (ch == '|' && parentheses == 0 && brackets == 0 && braces == 0) {
				stages.add(trimToken(source, start, i));
				start = i + 1;
				found = true;
			}
		}
		if (!found) return List.of(new Token(source, 0, source.length()));
		stages.add(trimToken(source, start, source.length()));
		return stages;
	}

	private static Token trimToken(String source, int start, int end) {
		while (start < end && Character.isWhitespace(source.charAt(start))) start++;
		while (end > start && Character.isWhitespace(source.charAt(end - 1))) end--;
		return new Token(source.substring(start, end), start, end);
	}

	private boolean isCollectionLiteralSyntax(String source, GenericModelType expectedType) {
		if (source.startsWith("[")) return true;
		if (source.startsWith("{") && (expectedType instanceof MapType || expectedType == EssentialTypes.TYPE_OBJECT)) return true;
		int opener = collectionOpener(source);
		return opener > 0;
	}

	private Maybe<ParsedValueExpression> resolveCollectionLiteral(String source, GenericModelType expectedType) {
		int opener = collectionOpener(source);
		if (opener < 0) return Maybe.empty(ParseError.create("Invalid collection literal"));
		char open = source.charAt(opener);
		char close = open == '[' ? ']' : '}';
		if (source.charAt(source.length() - 1) != close)
			return Maybe.empty(ParseError.create("Unterminated collection literal"));
		String decorator = source.substring(0, opener);
		CollectionType collectionType;
		if (!decorator.isEmpty()) {
			Maybe<CollectionType> resolved = resolveCollectionType(decorator);
			if (resolved.isUnsatisfied()) return Maybe.empty(resolved.whyUnsatisfied());
			collectionType = resolved.get();
			if (expectedType != null && expectedType != EssentialTypes.TYPE_OBJECT
					&& !expectedType.isAssignableFrom(collectionType))
				return Maybe.empty(ParseError.create("Explicit collection type " + collectionType.getTypeSignature()
						+ " is not assignable to " + expectedType.getTypeSignature()));
		} else if (open == '[') {
			if (expectedType instanceof CollectionType expected && !(expected instanceof MapType))
				collectionType = expected;
			else if (expectedType == EssentialTypes.TYPE_OBJECT)
				collectionType = GMF.getTypeReflection().getListType(EssentialTypes.TYPE_OBJECT);
			else return Maybe.empty(ParseError.create("Cannot infer list/set type without a collection-typed binding target"));
		} else {
			if (expectedType instanceof MapType expected) collectionType = expected;
			else if (expectedType == EssentialTypes.TYPE_OBJECT)
				collectionType = GMF.getTypeReflection().getMapType(EssentialTypes.TYPE_OBJECT, EssentialTypes.TYPE_OBJECT);
			else return Maybe.empty(ParseError.create("Cannot infer map type without a map-typed binding target"));
		}

		if (open == '[' && collectionType instanceof MapType
				|| open == '{' && !(collectionType instanceof MapType))
			return Maybe.empty(ParseError.create("Collection decorator does not match literal delimiters"));
		Reason nestingError = validateCollectionNesting(collectionType);
		if (nestingError != null) return Maybe.empty(nestingError);
		Located body = trim(source.substring(opener + 1, source.length() - 1),
				SourceRanges.subRange(activeRange, source, opener + 1, source.length() - 1));
		return collectionType instanceof MapType mapType
				? withinRange(body.range(), () -> resolveMapLiteral(body.text(), mapType))
				: withinRange(body.range(), () -> resolveLinearCollectionLiteral(body.text(), collectionType));
	}

	private int collectionOpener(String source) {
		if (source.startsWith("[") || source.startsWith("{")) return 0;
		if (!(source.startsWith("list<") || source.startsWith("set<") || source.startsWith("map<")))
			return -1;
		int depth = 0;
		for (int i = 0; i < source.length(); i++) {
			char ch = source.charAt(i);
			if (ch == '<') depth++;
			else if (ch == '>' && --depth == 0) {
				int opener = i + 1;
				return opener < source.length() && (source.charAt(opener) == '[' || source.charAt(opener) == '{')
						? opener : -1;
			}
		}
		return -1;
	}

	private Maybe<CollectionType> resolveCollectionType(String signature) {
		try {
			if (signature.startsWith("list<") && signature.endsWith(">"))
				return Maybe.complete(GMF.getTypeReflection().getListType(
						resolveLiteralType(signature.substring(5, signature.length() - 1))));
			if (signature.startsWith("set<") && signature.endsWith(">"))
				return Maybe.complete(GMF.getTypeReflection().getSetType(
						resolveLiteralType(signature.substring(4, signature.length() - 1))));
			if (signature.startsWith("map<") && signature.endsWith(">")) {
				String arguments = signature.substring(4, signature.length() - 1);
				int comma = genericComma(arguments);
				if (comma < 0) return Maybe.empty(ParseError.create("Map type requires key and value types"));
				return Maybe.complete(GMF.getTypeReflection().getMapType(
						resolveLiteralType(arguments.substring(0, comma)),
						resolveLiteralType(arguments.substring(comma + 1))));
			}
			return Maybe.empty(ParseError.create("Unknown collection type decorator: " + signature));
		} catch (IllegalArgumentException e) {
			return Maybe.empty(ParseError.create(e.getMessage()));
		}
	}

	private GenericModelType resolveLiteralType(String signature) {
		String typeName = signature.trim();
		Maybe<CollectionType> collection = resolveCollectionType(typeName);
		if (collection.isSatisfied()) return collection.get();
		GenericModelType type = resolveType(typeName);
		if (type != null) return type;
		throw new IllegalArgumentException("Unknown literal type: " + typeName);
	}

	private int genericComma(String source) {
		int depth = 0;
		for (int i = 0; i < source.length(); i++) {
			char ch = source.charAt(i);
			if (ch == '<') depth++;
			else if (ch == '>') depth--;
			else if (ch == ',' && depth == 0) return i;
		}
		return -1;
	}

	private Reason validateCollectionNesting(CollectionType type) {
		if (type instanceof MapType map) {
			if (map.getKeyType() instanceof CollectionType)
				return ParseError.create("Nested collection map key requires key type object");
			if (map.getValueType() instanceof CollectionType)
				return ParseError.create("Nested collection map value requires value type object");
		} else if (type.getCollectionElementType() instanceof CollectionType)
			return ParseError.create("Nested collection element requires element type object");
		return null;
	}

	private Maybe<ParsedValueExpression> resolveLinearCollectionLiteral(String body, CollectionType type) {
		List<Object> list = new PlainList<>((com.braintribe.model.generic.reflection.ListType)
				(type.getCollectionKind() == CollectionType.CollectionKind.set
						? GMF.getTypeReflection().getListType(type.getCollectionElementType()) : type));
		for (Token token : tokenizeLocated(body)) {
			TextRange range = SourceRanges.subRange(activeRange, body, token.start(), token.end());
			Maybe<ParsedValueExpression> element = withinRange(range,
					() -> resolveValueExpression(token.text(), true, type.getCollectionElementType()));
			if (element.isUnsatisfied()) return Maybe.empty(element.whyUnsatisfied());
			if (!NullLiteral.is(element.get().value()) && (element.get().type() == null
					|| !type.getCollectionElementType().isAssignableFrom(element.get().type())))
				return Maybe.empty(ParseError.create("Collection element is not assignable to "
						+ type.getCollectionElementType().getTypeSignature()));
			list.add(NullLiteral.materialize(element.get().value()));
		}
		Object value = type.getCollectionKind() == CollectionType.CollectionKind.set
				? new PlainSet<>((com.braintribe.model.generic.reflection.SetType) type, list) : list;
		return Maybe.complete(new ParsedValueExpression(value, type));
	}

	private Maybe<ParsedValueExpression> resolveStructuredLiteral(String source, GenericModelType expectedType) {
		if (!source.startsWith("{") || !source.endsWith("}"))
			return Maybe.empty(ParseError.create("Not an inferred structured literal"));
		Located body = trim(source.substring(1, source.length() - 1),
				SourceRanges.subRange(activeRange, source, 1, source.length() - 1));
		if (expectedType instanceof EntityType<?> entityType) {
			Maybe<List<ParsedArgument>> arguments = parseArguments(body.text(), body.range(), entityType);
			if (arguments.isUnsatisfied())
				return Maybe.empty(arguments.whyUnsatisfied());
			Maybe<? extends GenericEntity> entity = reflectedEntityNormalizer.normalize(entityType, arguments.get());
			return entity.isSatisfied()
					? Maybe.complete(new ParsedValueExpression(entity.get(), entityType))
					: Maybe.empty(entity.whyUnsatisfied());
		}
		if (expectedType instanceof MapType mapType)
			return withinRange(body.range(), () -> resolveMapLiteral(body.text(), mapType));
		return Maybe.empty(ParseError.create("Cannot infer structured literal type without an entity- or map-typed binding target"));
	}

	private Maybe<ParsedValueExpression> resolveMapLiteral(String body, MapType mapType) {
		List<Token> tokens = tokenizeLocated(body);
		Map<Object, Object> values = new PlainMap<>(mapType);
		for (int i = 0; i < tokens.size();) {
			Token keyToken = tokens.get(i++);
			if (!keyToken.text().endsWith(":"))
				return Maybe.empty(ParseError.create("Map key must be followed by ':'"));
			if (i >= tokens.size())
				return Maybe.empty(ParseError.create("Missing value for map key " + keyToken.text()));
			Token valueToken = tokens.get(i++);
			TextRange keyRange = SourceRanges.subRange(activeRange, body, keyToken.start(), keyToken.end() - 1);
			TextRange valueRange = SourceRanges.subRange(activeRange, body, valueToken.start(), valueToken.end());
			Maybe<ParsedValueExpression> key = withinRange(keyRange, () -> resolveValueExpression(
					keyToken.text().substring(0, keyToken.text().length() - 1), true, mapType.getKeyType()));
			Maybe<ParsedValueExpression> value = withinRange(valueRange,
					() -> resolveValueExpression(valueToken.text(), true, mapType.getValueType()));
			if (key.isUnsatisfied()) return Maybe.empty(key.whyUnsatisfied());
			if (value.isUnsatisfied()) return Maybe.empty(value.whyUnsatisfied());
			if (!NullLiteral.is(key.get().value())
					&& (key.get().type() == null || !mapType.getKeyType().isAssignableFrom(key.get().type())))
				return Maybe.empty(ParseError.create("Map key is not assignable to " + mapType.getKeyType().getTypeSignature()));
			if (!NullLiteral.is(value.get().value())
					&& (value.get().type() == null || !mapType.getValueType().isAssignableFrom(value.get().type())))
				return Maybe.empty(ParseError.create("Map value is not assignable to " + mapType.getValueType().getTypeSignature()));
			Object materializedKey = NullLiteral.materialize(key.get().value());
			if (values.containsKey(materializedKey))
				return Maybe.empty(ParseError.create("Duplicate map key: " + keyToken.text().substring(0,
						keyToken.text().length() - 1)));
			values.put(materializedKey, NullLiteral.materialize(value.get().value()));
		}
		return Maybe.complete(new ParsedValueExpression(values, mapType));
	}

	private Maybe<ParsedValueExpression> resolveEntityExpression(String source, GenericModelType expectedType) {
		if (!source.startsWith("(") || !source.endsWith(")"))
			return Maybe.empty(ParseError.create("Not a value descriptor expression: " + source));

		Located body = trim(source.substring(1, source.length() - 1),
				SourceRanges.subRange(activeRange, source, 1, source.length() - 1));
		if (body.text().isEmpty())
			return Maybe.empty(ParseError.create("Empty value descriptor expression"));

		String name = firstToken(body.text());
		int argumentsStart = skipWhitespace(body.text(), name.length());
		String arguments = body.text().substring(argumentsStart);
		TextRange argumentsRange = SourceRanges.subRange(body.range(), body.text(), argumentsStart, body.text().length());
		Maybe<? extends EntityType<?>> type = resolveExpressionEntityType(name, expectedType);
		if (type.isUnsatisfied())
			return Maybe.empty(type.whyUnsatisfied());

		Maybe<? extends GenericEntity> entity = resolveExplicitEntityValue(type.get(), arguments, argumentsRange);
		if (entity.isUnsatisfied()) return Maybe.empty(entity.whyUnsatisfied());
		GenericEntity value = entity.get();
		if (value instanceof TypeReference reference) {
			String signature = normalizeTypeSignature(reference.getTypeSignature());
			GenericModelType referencedType = resolveType(signature);
			if (referencedType == null)
				return Maybe.empty(ParseError.create("Unknown type reference: " + reference.getTypeSignature()));
			reference.setTypeSignature(referencedType.getTypeSignature());
		}
		GenericModelType valueType;
		if (value instanceof ValueDescriptor vd) {
			@SuppressWarnings("unchecked")
			VdEvaluator<ValueDescriptor, Object> evaluator =
					(VdEvaluator<ValueDescriptor, Object>) registry.findVdEvaluator(vd.entityType());
			if (evaluator != null) {
				Reason completion = evaluator.complete(this, vd);
				if (completion != null) return Maybe.empty(completion);
			}
			valueType = vd.valueType();
			descriptorTypes.put(vd, valueType);
		} else valueType = type.get();
		return Maybe.complete(new ParsedValueExpression(value, valueType));
	}

	private static String normalizeTypeSignature(String source) {
		return source == null ? null : source.replaceAll("\\s+", "");
	}

	@SuppressWarnings("unchecked")
	private Maybe<? extends GenericEntity> resolveExplicitEntityValue(EntityType<?> type, String arguments,
			TextRange argumentsRange) {
		dev.hiconic.template.api.ScalarEntityParser<?> scalarParser = registry.findScalarParser(type);
		if (scalarParser != null && !startsWithNamedProperty(arguments, type)) {
			if (arguments.isBlank())
				return Maybe.empty(ParseError.create("Missing scalar value for " + type.getShortName()));
			return withinRange(argumentsRange,
					() -> ((dev.hiconic.template.api.ScalarEntityParser<GenericEntity>) scalarParser).parse(arguments, this));
		}
		Maybe<List<ParsedArgument>> parsedArguments = parseArguments(arguments, argumentsRange, type);
		return parsedArguments.isSatisfied()
				? reflectedEntityNormalizer.normalize(type, parsedArguments.get())
				: Maybe.empty(parsedArguments.whyUnsatisfied());
	}

	private static boolean startsWithNamedProperty(String source, EntityType<?> type) {
		List<Token> tokens = tokenizeLocated(source);
		if (tokens.isEmpty()) return false;
		String first = tokens.get(0).text();
		if (!first.endsWith(":") || first.length() == 1 || first.contains("::")) return false;
		return type.findProperty(toCamelCase(first.substring(0, first.length() - 1))) != null;
	}

	@SuppressWarnings("unchecked")
	private Maybe<? extends EntityType<?>> resolveExpressionEntityType(String name, GenericModelType expectedType) {
		Maybe<EntityType<? extends ValueDescriptor>> vd = resolveValueDescriptorType(name);
		if (vd.isSatisfied()) return vd;
		List<EntityType<?>> registeredAliases = registry.scalarEntityTypes().stream()
				.filter(type -> TemplateTypeNameResolver.hasExplicitAlias(type, name))
				.toList();
		if (registeredAliases.size() == 1) return Maybe.complete(registeredAliases.get(0));
		if (registeredAliases.size() > 1)
			return Maybe.empty(ParseError.create("Ambiguous entity type alias '" + name + "': "
					+ registeredAliases.stream().map(GenericModelType::getTypeSignature).sorted().toList()));
		GenericModelType type = resolveType(name);
		return type instanceof EntityType<?> entityType
				? Maybe.complete(entityType)
				: Maybe.empty(ParseError.create("Unknown entity type: " + name));
	}

	private Maybe<ParsedValueExpression> resolvePath(String path) {
		String source = path.trim();
		int cursor = identifierEnd(source, 0);
		if (cursor == 0)
			return Maybe.empty(ParseError.create("Invalid property path: " + path));
		String rootName = source.substring(0, cursor);
		Maybe<GenericModelType> type = validationScope.resolve(rootName);
		if (type.isUnsatisfied())
			return Maybe.empty(type.whyUnsatisfied());

		GenericModelType currentType = type.get();
		TemplateVariable variable = TemplateVariable.T.create();
		variable.setName(rootName);
		variable.setSymbol(symbols.intern(rootName));
		variable.setTypeSignature(currentType.getTypeSignature());
		descriptorTypes.put(variable, currentType);
		if (cursor == source.length())
			return Maybe.complete(new ParsedValueExpression(variable, currentType));

		TemplatePropertyPath propertyPath = TemplatePropertyPath.T.create();
		propertyPath.setRoot(variable);
		List<PathAccess> accesses = new ArrayList<>();
		while (cursor < source.length()) {
			if (source.startsWith("?.", cursor) || source.charAt(cursor) == '.') {
				boolean optional = source.startsWith("?.", cursor);
				cursor += optional ? 2 : 1;
				int segmentStart = cursor;
				cursor = identifierEnd(source, cursor);
				if (cursor == segmentStart)
					return Maybe.empty(ParseError.create("Expected property name in path: " + source));
				String segment = source.substring(segmentStart, cursor);
				if (!(currentType instanceof EntityType<?> entityType))
					return Maybe.empty(ParseError.create("Cannot access property '" + segment + "' on "
							+ currentType.getTypeSignature()));
				Property reflected = entityType.findProperty(segment);
				if (reflected == null)
					return Maybe.empty(ParseError.create("Unknown property '" + segment + "' on "
							+ entityType.getTypeSignature()));
				PropertyReference reference = PropertyReference.T.create();
				reference.setSymbol(symbols.intern(segment));
				reference.setDeclaringType(DefinitionTools.type(currentType.getTypeSignature()));
				reference.setType(DefinitionTools.type(reflected.getType().getTypeSignature()));
				reference.setResolvedProperty(reflected);
				PropertyAccess access = PropertyAccess.T.create();
				access.setProperty(reference);
				access.setOptional(optional);
				access.setSource(segment);
				access.setResultType(reference.getType());
				access.setSourceRange(SourceRanges.subRange(activeRange, source, segmentStart, cursor));
				accesses.add(access);
				currentType = reflected.getType();
				continue;
			}

			boolean optional = source.startsWith("?[", cursor);
			if (!optional && source.charAt(cursor) != '[')
				return Maybe.empty(ParseError.create("Unexpected token in path at offset " + cursor + ": " + source));
			int accessStart = cursor;
			cursor += optional ? 2 : 1;
			int expressionStart = cursor;
			int close = bracketEnd(source, expressionStart);
			if (close < 0) return Maybe.empty(ParseError.create("Unterminated collection access in path: " + source));
			Located expression = trim(source.substring(expressionStart, close),
					SourceRanges.subRange(activeRange, source, expressionStart, close));
			if (expression.text().isEmpty())
				return Maybe.empty(ParseError.create("Collection access requires an index or key"));
			TextRange accessRange = SourceRanges.subRange(activeRange, source, accessStart, close + 1);
			if (currentType instanceof ListType listType) {
				Maybe<ParsedValueExpression> index = withinRange(expression.range(), () -> resolveValueExpression(
						expression.text(), true, SimpleTypes.TYPE_INTEGER));
				if (index.isUnsatisfied()) return Maybe.empty(index.whyUnsatisfied());
				if (NullLiteral.is(index.get().value()) || index.get().type() == null
						|| !SimpleTypes.TYPE_INTEGER.isAssignableFrom(index.get().type()))
					return Maybe.empty(ParseError.create("List index must be integer but got "
							+ index.get().type().getTypeSignature()));
				ListIndexAccess access = ListIndexAccess.T.create();
				set(ListIndexAccess.T.findProperty("index"), access, index.get());
				access.setOptional(optional);
				access.setSource(expression.text());
				currentType = listType.getCollectionElementType();
				access.setResultType(DefinitionTools.type(currentType.getTypeSignature()));
				access.setSourceRange(accessRange);
				accesses.add(access);
			} else if (currentType instanceof MapType mapType) {
				Maybe<ParsedValueExpression> key = withinRange(expression.range(), () -> resolveValueExpression(
						expression.text(), true, mapType.getKeyType()));
				if (key.isUnsatisfied()) return Maybe.empty(key.whyUnsatisfied());
				if (!NullLiteral.is(key.get().value()) && (key.get().type() == null
						|| !mapType.getKeyType().isAssignableFrom(key.get().type())))
					return Maybe.empty(ParseError.create("Map key is not assignable to "
							+ mapType.getKeyType().getTypeSignature()));
				MapKeyAccess access = MapKeyAccess.T.create();
				set(MapKeyAccess.T.findProperty("key"), access, key.get());
				access.setOptional(optional);
				access.setSource(expression.text());
				currentType = mapType.getValueType();
				access.setResultType(DefinitionTools.type(currentType.getTypeSignature()));
				access.setSourceRange(accessRange);
				accesses.add(access);
			} else {
				return Maybe.empty(ParseError.create("Type " + currentType.getTypeSignature()
						+ " does not support indexed/key access"));
			}
			cursor = close + 1;
		}
		propertyPath.setAccesses(accesses);
		propertyPath.setType(DefinitionTools.type(currentType.getTypeSignature()));
		propertyPath.setSourceRange(activeRange);
		descriptorTypes.put(propertyPath, currentType);
		return Maybe.complete(new ParsedValueExpression(propertyPath, currentType));
	}

	private static int identifierEnd(String source, int start) {
		if (start >= source.length() || !(Character.isLetter(source.charAt(start)) || source.charAt(start) == '_')) return start;
		int cursor = start + 1;
		while (cursor < source.length() && (Character.isLetterOrDigit(source.charAt(cursor))
				|| source.charAt(cursor) == '_')) cursor++;
		return cursor;
	}

	private static int bracketEnd(String source, int start) {
		int nested = 0;
		char quote = 0;
		for (int i = start; i < source.length(); i++) {
			char ch = source.charAt(i);
			if (quote != 0) {
				if (ch == quote && !isEscaped(source, i)) quote = 0;
			} else if (ch == '"' || ch == '\'') quote = ch;
			else if (ch == '[' || ch == '(' || ch == '{') nested++;
			else if (ch == ']' && nested == 0) return i;
			else if (ch == ']' || ch == ')' || ch == '}') nested--;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private Maybe<EntityType<? extends DirectiveNode>> resolveDirectiveType(String name) {
		EntityType<? extends InstructionNode> standard = standardInstructions.get(name);
		if (standard != null)
			return Maybe.complete(standard);
		if (expertTypeNameResolver == null)
			return Maybe.empty(ParseError.create("Unknown directive: " + name));

		Maybe<EntityType<?>> type = expertTypeNameResolver.resolve(name, TemplateTypeNameResolver.Usage.INSTRUCTION);
		if (type.isUnsatisfied())
			return Maybe.empty(type.whyUnsatisfied());
		return Maybe.complete((EntityType<? extends DirectiveNode>) type.get());
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

		if (expertTypeNameResolver == null)
			return Maybe.empty(ParseError.create("Unknown value descriptor: " + name));
		Maybe<EntityType<?>> type = expertTypeNameResolver.resolve(name, TemplateTypeNameResolver.Usage.VALUE_DESCRIPTOR);
		if (type.isUnsatisfied())
			return Maybe.empty(type.whyUnsatisfied());
		return Maybe.complete((EntityType<? extends ValueDescriptor>) type.get());
	}

	private Maybe<List<ParsedArgument>> parseArguments(String source, TextRange sourceRange) {
		List<ParsedArgument> arguments = new ArrayList<>();
		List<Token> tokens = mergePipelines(source, tokenizeLocated(source));
		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			String name = null;
			Token value = token;
			if (token.text().endsWith(":") && token.text().length() > 1 && !token.text().contains("::")) {
				name = toCamelCase(token.text().substring(0, token.text().length() - 1));
				if (++i >= tokens.size())
					return Maybe.empty(ParseError.create("Missing value for argument " + token.text()));
				value = tokens.get(i);
			}
			arguments.add(new ParsedArgument(name, value.text(),
					SourceRanges.subRange(sourceRange, source, value.start(), value.end())));
		}
		return Maybe.complete(arguments);
	}

	private static List<Token> mergePipelines(String source, List<Token> tokens) {
		if (tokens.stream().noneMatch(token -> "|".equals(token.text()))) return tokens;
		List<Token> merged = new ArrayList<>();
		for (int i = 0; i < tokens.size(); i++) {
			Token first = tokens.get(i);
			int end = first.end();
			while (i + 1 < tokens.size() && "|".equals(tokens.get(i + 1).text())) {
				if (i + 2 >= tokens.size()) { end = tokens.get(++i).end(); break; }
				end = tokens.get(i + 2).end();
				i += 2;
			}
			merged.add(new Token(source.substring(first.start(), end), first.start(), end));
		}
		return merged;
	}

	private Maybe<List<ParsedArgument>> parseArguments(String source, TextRange sourceRange, EntityType<?> type) {
		com.braintribe.model.generic.annotation.meta.PositionalArguments positional =
				type.getJavaType().getAnnotation(com.braintribe.model.generic.annotation.meta.PositionalArguments.class);
		if (positional == null || positional.value().length == 0) return parseArguments(source, sourceRange);
		String remainderProperty = positional.value()[positional.value().length - 1];
		Property property = type.findProperty(remainderProperty);
		if (property == null || !SourceText.T.isAssignableFrom(property.getType())) return parseArguments(source, sourceRange);

		int prefixCount = positional.value().length - 1;
		List<Token> tokens = mergePipelines(source, tokenizeLocated(source));
		if (tokens.size() < prefixCount)
			return Maybe.empty(ParseError.create("Missing positional argument before remainder property '"
					+ remainderProperty + "' for " + type.getShortName()));
		List<ParsedArgument> result = new ArrayList<>();
		int cursor = 0;
		for (int i = 0; i < prefixCount; i++) {
			Token token = tokens.get(i);
			if (token.text().endsWith(":"))
				return Maybe.empty(ParseError.create("Named arguments are not allowed before a remainder property"));
			cursor = token.end();
			result.add(new ParsedArgument(null, token.text(),
					SourceRanges.subRange(sourceRange, source, token.start(), token.end())));
		}
		while (cursor < source.length() && Character.isWhitespace(source.charAt(cursor))) cursor++;
		SourceText text = SourceText.T.create();
		text.setValue(source.substring(cursor));
		result.add(new ParsedArgument(null, text, SourceText.T,
				SourceRanges.subRange(sourceRange, source, cursor, source.length())));
		return Maybe.complete(result);
	}

	private static void set(Property property, GenericEntity entity, ParsedValueExpression value) {
		if (value.value() instanceof ValueDescriptor descriptor)
			property.setVdDirect(entity, descriptor);
		else
			property.setDirect(entity, NullLiteral.materialize(value.value()));
	}

	private static String firstToken(String text) {
		int index = 0;
		while (index < text.length() && !Character.isWhitespace(text.charAt(index)))
			index++;
		return text.substring(0, index);
	}

	private static List<String> tokenize(String source) {
		return tokenizeLocated(source).stream().map(Token::text).toList();
	}

	private static List<Token> tokenizeLocated(String source) {
		List<Token> tokens = new ArrayList<>();
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
				tokens.add(new Token(source.substring(start, i), start, i));
				start = -1;
			}
		}
		if (start >= 0)
			tokens.add(new Token(source.substring(start), start, source.length()));
		return tokens;
	}

	private record Token(String text, int start, int end) {
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
}
