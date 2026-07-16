package dev.hiconic.template.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.bvd.navigation.PropertyPath;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

import dev.hiconic.template.api.TemplateParserOptions;
import dev.hiconic.template.api.TemplateParserResolver;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.ConfigurableTemplateExpertRegistry;
import dev.hiconic.template.impl.StandardTemplateExperts;
import dev.hiconic.template.impl.parser.ParsedValueExpression;
import dev.hiconic.template.impl.parser.StandardOutputExpressionParser;
import dev.hiconic.template.impl.parser.StandardTemplateParser;
import dev.hiconic.template.impl.parser.TemplateValueExpressionResolver;
import dev.hiconic.template.model.core.CommentNode;
import dev.hiconic.template.model.core.ErrorNode;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.SequenceNode;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.TextNode;
import dev.hiconic.template.model.core.instr.ForEach;
import dev.hiconic.template.model.core.instr.If;
import dev.hiconic.template.model.core.instr.Case;
import dev.hiconic.template.model.core.instr.Default;
import dev.hiconic.template.model.core.instr.Switch;
import dev.hiconic.template.model.core.output.FormatDate;
import dev.hiconic.template.model.core.output.HtmlEsc;
import dev.hiconic.template.model.core.output.RawOutput;
import dev.hiconic.template.model.core.output.UrlComponentEscape;
import dev.hiconic.template.model.core.vd.UnaryOperation;
import dev.hiconic.template.model.parse.TemplateParseError;
import dev.hiconic.template.model.parse.TextRange;
import dev.hiconic.template.test.model.TestBlockInstruction;
import dev.hiconic.template.test.model.TemplateTestInput;

public class StandardTemplateParserTest {
	private static final ValidationContext DECLARED_TYPES = new ValidationContext() {
		@Override
		public GenericModelType getType(GenericEntity entity, Property property) {
			return property.getType();
		}
	};

	@Test
	public void parsesPlainText() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, new TestResolver()).parse("hello");

		assertTrue(maybe.isSatisfied());
		assertTrue(maybe.get() instanceof TextNode);
		assertEquals("hello", ((TextNode) maybe.get()).getText());
	}

	@Test
	public void escapedTemplateOpenersRemainLiteralText() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, new TestResolver())
				.parse("\\${value} \\%(if true) \\#{directive} \\\\");

		assertTrue(maybe.isSatisfied());
		assertEquals("${value} %(if true) #{directive} \\", ((TextNode) maybe.get()).getText());
	}

	@Test
	public void constructEndIgnoresBracesInsideQuotedAndNestedValues() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, new TestResolver())
				.parse("${string:\"a } b { c\"}");

		assertTrue(maybe.isSatisfied());
		assertEquals("string:\"a } b { c\"", ((OutputNode) maybe.get()).getOutput().getText());
	}

	@Test
	public void parsesCommentsAsSilentTemplateNodes() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, new TestResolver())
				.parse("a#{ internal comment }b");

		assertTrue(maybe.isSatisfied());
		SequenceNode root = (SequenceNode) maybe.get();
		assertEquals(3, root.getNodes().size());
		assertEquals("a", ((TextNode) root.getNodes().get(0)).getText());
		assertEquals(" internal comment ", ((CommentNode) root.getNodes().get(1)).getText().getValue());
		assertEquals("b", ((TextNode) root.getNodes().get(2)).getText());
	}

	@Test
	public void resolvesOutputBetweenTextFragments() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, new TestResolver()).parse("a${value}b");

		assertTrue(maybe.isSatisfied());
		SequenceNode root = (SequenceNode) maybe.get();
		assertEquals(3, root.getNodes().size());
		assertEquals("a", ((TextNode) root.getNodes().get(0)).getText());
		assertEquals("value", ((OutputNode) root.getNodes().get(1)).getOutput().getText());
		assertEquals("b", ((TextNode) root.getNodes().get(2)).getText());
	}

	@Test
	public void wiresPrimaryAndElseBlocksBeforeCompletion() {
		TestResolver resolver = new TestResolver();
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, resolver)
				.parse("%(if true)yes%(else)no%(end)");

		assertTrue(maybe.isSatisfied());
		If ifNode = (If) maybe.get();
		assertTrue(ifNode.getCondition());
		assertEquals("yes", firstText(ifNode.getBlock()));
		assertEquals("no", firstText(ifNode.getElse()));
		assertTrue("Completion must see both wired blocks", resolver.completedWiredIf);
	}

	@Test
	public void wiresCustomSecondaryBlockFromTestModelWithoutParserSpecialCase() {
		TestResolver resolver = new TestResolver();
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, resolver)
				.parse("%(test true)primary%(fallback)secondary%(end)");

		assertTrue(maybe.isSatisfied() ? "" : reasonTree(maybe.whyUnsatisfied()), maybe.isSatisfied());
		TestBlockInstruction instruction = (TestBlockInstruction) maybe.get();
		assertEquals("primary", firstText(instruction.getBlock()));
		assertEquals("secondary", firstText(instruction.getFallback()));
		assertTrue(resolver.completedWiredTestInstruction);
	}

	@Test
	public void wiresRepeatedSwitchCasesAndDefaultBlock() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, new TestResolver())
				.parse("%(switch green)%(case red)red%(case green)green%(default)other%(end)");

		assertTrue(maybe.isSatisfied() ? "" : reasonTree(maybe.whyUnsatisfied()), maybe.isSatisfied());
		Switch switchNode = (Switch) maybe.get();
		assertEquals(3, switchNode.getCases().size());
		assertEquals("red", ((Case) switchNode.getCases().get(0)).getValue());
		assertEquals("green", firstText(switchNode.getCases().get(1).getBlock()));
		assertTrue(switchNode.getCases().get(2) instanceof Default);
		assertEquals("other", firstText(switchNode.getCases().get(2).getBlock()));
	}

	@Test
	public void strictModeReturnsNoTemplateAndPositionedReason() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, new TestResolver())
				.parse("line 1\n${fail}");

		assertTrue(maybe.isEmpty());
		assertTrue(maybe.isUnsatisfiedBy(ParseError.T));

		Reason aggregate = maybe.whyUnsatisfied();
		TemplateParseError error = (TemplateParseError) aggregate.getReasons().get(0);
		assertEquals(2, error.getRange().getStart().getLine());
		assertEquals(1, error.getRange().getStart().getColumn());
		assertEquals("${fail}", error.getFragment());
		assertFalse(error.getReasons().isEmpty());
	}

	@Test
	public void substituteModeReturnsIncompleteTemplateWithVisibleErrorNode() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.SUBSTITUTE, new TestResolver())
				.parse("before %(unknown)hidden%(end) after");

		assertTrue(maybe.isIncomplete());
		SequenceNode root = (SequenceNode) maybe.value();
		assertEquals(5, root.getNodes().size());
		ErrorNode error = (ErrorNode) root.getNodes().get(1);
		assertTrue(error.getText().contains("TEMPLATE ERROR"));
		assertTrue(error.getText().contains("line 1, column 8"));
		assertEquals("hidden", ((TextNode) root.getNodes().get(2)).getText());
		assertTrue(root.getNodes().get(3) instanceof ErrorNode);
		assertEquals(" after", ((TextNode) root.getNodes().get(4)).getText());
	}

	@Test
	public void unknownDirectiveInsideBlockDoesNotConsumeParentEndMarker() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.SUBSTITUTE, new TestResolver())
				.parse("%(if true)before %(unknown) after%(end)");

		assertTrue(maybe.isIncomplete());
		String reason = reasonTree(maybe.whyUnsatisfied());
		assertTrue(reason, reason.contains("Unknown directive: unknown"));
		assertTrue(reason, !reason.contains("Missing '%(end)'"));
	}

	@Test
	public void silenceModeKeepsDiagnosticsButSuppressesReplacementText() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.SILENCE, new TestResolver()).parse("${fail}");

		assertTrue(maybe.isIncomplete());
		ErrorNode error = (ErrorNode) maybe.value();
		assertEquals("", error.getText());
		assertNotNull(error.getRange());
		assertTrue(maybe.whyUnsatisfied().getReasons().get(0) instanceof TemplateParseError);
	}

	@Test
	public void reportsUnterminatedConstructAtItsOpeningPosition() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.SUBSTITUTE, new TestResolver())
				.parse("first\n  ${broken");

		assertTrue(maybe.isIncomplete());
		TemplateParseError error = (TemplateParseError) maybe.whyUnsatisfied().getReasons().get(0);
		assertEquals(2, error.getRange().getStart().getLine());
		assertEquals(3, error.getRange().getStart().getColumn());
		assertEquals("${broken", error.getFragment());
	}

	@Test
	public void completionFailuresArePositionedAndSubstituted() {
		TestResolver resolver = new TestResolver();
		resolver.failCompletion = true;

		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.SUBSTITUTE, resolver).parse("${value}");

		assertTrue(maybe.isIncomplete());
		ErrorNode node = (ErrorNode) maybe.value();
		assertTrue(node.getMessage().contains("Completion or validation failed"));
		TemplateParseError error = (TemplateParseError) maybe.whyUnsatisfied().getReasons().get(0);
		assertTrue(error.getReasons().get(0) instanceof InvalidArgument);
	}

	@Test
	public void incompleteResolverResultIsNotMistakenForSuccess() {
		TestResolver resolver = new TestResolver();
		resolver.returnIncompleteOutput = true;

		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.SUBSTITUTE, resolver).parse("${value}");

		assertTrue(maybe.isIncomplete());
		assertTrue(maybe.value() instanceof ErrorNode);
	}

	@Test
	public void resolvesTypedInputPropertyPath() {
		TypedInputResolver resolver = new TypedInputResolver();

		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, resolver).parse("${input.title}");

		assertTrue(maybe.isSatisfied());
		assertEquals(List.of("input.title"), resolver.resolvedPaths);
	}

	@Test
	public void resolvesForEachVariablesAndNestedPropertyPathsInItsBlock() {
		TypedInputResolver resolver = new TypedInputResolver();
		String source = "%(for-each input.persons as: person index: i)"
				+ "${i}:${person.name}-${person.address.city}"
				+ "%(empty)none%(end)";

		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, resolver).parse(source);

		assertTrue(maybe.isSatisfied());
		ForEach forEach = (ForEach) maybe.get();
		assertEquals("person", dev.hiconic.template.impl.parser.DefinitionTools.name(forEach.getVariable()));
		assertEquals("i", dev.hiconic.template.impl.parser.DefinitionTools.name(forEach.getIndexVariable()));
		assertNotNull(forEach.getBlock());
		assertEquals("none", firstText(forEach.getEmpty()));
		assertEquals(List.of("input.persons", "i", "person.name", "person.address.city"), resolver.resolvedPaths);
	}

	@Test
	public void substitutesUnknownTypedPropertyPath() {
		TypedInputResolver resolver = new TypedInputResolver();

		Maybe<TemplateNode> maybe =
				parser(TemplateParserOptions.SUBSTITUTE, resolver).parse("Hello ${input.unknown}");

		assertTrue(maybe.isIncomplete());
		assertTrue(((SequenceNode) maybe.value()).getNodes().get(1) instanceof ErrorNode);
		assertTrue(maybe.whyUnsatisfied().getReasons().get(0) instanceof TemplateParseError);
	}

	@Test
	public void loopVariableIsNotVisibleInEmptyBlock() {
		TypedInputResolver resolver = new TypedInputResolver();
		String source = "%(for-each input.persons as: person)"
				+ "${person.name}%(empty)${person.name}%(end)";

		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.SUBSTITUTE, resolver).parse(source);

		assertTrue(maybe.isIncomplete());
		ForEach forEach = (ForEach) maybe.value();
		assertTrue(forEach.getEmpty() instanceof ErrorNode);
	}

	@Test
	public void outputPipelineAutoCompletesDateToHtmlSafeOutput() {
		Maybe<TemplateNode> maybe = parser(TemplateParserOptions.STRICT, new PipelineResolver()).parse("${input.birthday}");

		assertTrue(maybe.isSatisfied() ? "" : reasonTree(maybe.whyUnsatisfied()), maybe.isSatisfied());
		ValueDescriptor html = outputDescriptor(maybe.get());
		assertTrue(html instanceof HtmlEsc);
		ValueDescriptor format = nestedOperandDescriptor((UnaryOperation) html);
		assertTrue(format instanceof FormatDate);
	}

	private static StandardTemplateParser parser(TemplateParserOptions options, TestResolver resolver) {
		return new StandardTemplateParser(resolver, DECLARED_TYPES, options);
	}

	private static String firstText(TemplateNode node) {
		return ((TextNode) node).getText();
	}

	private static ValueDescriptor outputDescriptor(TemplateNode node) {
		ValueDescriptor descriptor = OutputNode.output.property().getVdDirect((OutputNode) node);
		assertTrue(descriptor instanceof UnaryOperation);
		return descriptor;
	}

	private static ValueDescriptor nestedOperandDescriptor(UnaryOperation operation) {
		ValueDescriptor descriptor = UnaryOperation.operand.property().getVdDirect(operation);
		assertTrue(descriptor instanceof ValueDescriptor);
		return descriptor;
	}

	private static String reasonTree(Reason reason) {
		StringBuilder result = new StringBuilder();
		appendReason(result, reason, "");
		return result.toString();
	}

	private static void appendReason(StringBuilder result, Reason reason, String indent) {
		result.append(indent).append(reason.entityType().getShortName())
				.append(": ").append(reason.getText()).append('\n');
		for (Reason cause : reason.getReasons())
			appendReason(result, cause, indent + "  ");
	}

	private static class TestResolver implements TemplateParserResolver {
		private boolean completedWiredIf;
		private boolean completedWiredTestInstruction;
		private boolean failCompletion;
		private boolean returnIncompleteOutput;

		@Override
		public Maybe<OutputNode> resolveOutput(String expression, TextRange range) {
			OutputNode output = OutputNode.T.create();
			RawOutput safeOutput = RawOutput.T.create();
			safeOutput.setText(expression);
			output.setOutput(safeOutput);

			if ("fail".equals(expression))
				return Maybe.empty(ParseError.create("Deliberate output failure"));
			if (returnIncompleteOutput)
				return Maybe.incomplete(output, ParseError.create("Deliberately incomplete output"));
			return Maybe.complete(output);
		}

		@Override
		public Maybe<? extends TemplateNode> resolveDirective(char sigil, String invocation, boolean blockFree, TextRange range) {
			if (invocation.startsWith("if ")) {
				If node = If.T.create();
				node.setCondition(Boolean.parseBoolean(invocation.substring(3).trim()));
				return Maybe.complete(node);
			}
			if (invocation.startsWith("test ")) {
				TestBlockInstruction node = TestBlockInstruction.T.create();
				node.setCondition(Boolean.parseBoolean(invocation.substring(5).trim()));
				return Maybe.complete(node);
			}
			if (invocation.startsWith("switch ")) {
				Switch node = Switch.T.create();
				node.setValue(invocation.substring(7).trim());
				node.setCases(new ArrayList<>());
				return Maybe.complete(node);
			}
			return Maybe.empty(ParseError.create("Unknown directive: " + invocation));
		}

		@Override
		public java.util.Set<String> clauseMarkers(GenericModelType expectedClauseType) {
			return java.util.Set.of("case", "default");
		}

		@Override
		public Maybe<? extends GenericEntity> resolveClause(GenericModelType expectedClauseType, String marker,
				String invocation, TextRange range) {
			if ("case".equals(marker)) {
				Case node = Case.T.create();
				node.setValue(invocation.trim());
				return Maybe.complete(node);
			}
			if ("default".equals(marker))
				return Maybe.complete(Default.T.create());
			return Maybe.empty(ParseError.create("Unknown clause: " + marker));
		}

		@Override
		public Reason completeAndValidate(ValidationContext context, TemplateNode node, TextRange range) {
			if (node instanceof If ifNode)
				completedWiredIf = ifNode.getBlock() != null && ifNode.getElse() != null;
			if (node instanceof TestBlockInstruction testNode)
				completedWiredTestInstruction = testNode.getBlock() != null && testNode.getFallback() != null;
			return failCompletion && node instanceof OutputNode
					? InvalidArgument.create("Deliberate completion failure")
					: null;
		}
	}

	private static class TypedInputResolver extends TestResolver {
		private final Deque<Map<String, GenericModelType>> scopes = new ArrayDeque<>();
		private final Map<ForEach, GenericModelType> elementTypes = new IdentityHashMap<>();
		private final List<String> resolvedPaths = new ArrayList<>();

		private TypedInputResolver() {
			scopes.push(Map.of("input", TemplateTestInput.T));
		}

		@Override
		public Maybe<OutputNode> resolveOutput(String expression, TextRange range) {
			Maybe<GenericModelType> type = resolvePath(expression);
			if (type.isEmpty())
				return Maybe.empty(type.whyUnsatisfied());

			resolvedPaths.add(expression);
			return super.resolveOutput(expression, range);
		}

		@Override
		public Maybe<? extends TemplateNode> resolveDirective(
				char sigil, String invocation, boolean blockFree, TextRange range) {
			if (!invocation.startsWith("for-each "))
				return super.resolveDirective(sigil, invocation, blockFree, range);

			String[] tokens = invocation.trim().split("\\s+");
			if (tokens.length < 2)
				return Maybe.empty(ParseError.create("Missing for-each iterable"));

			String iterablePath = tokens[1];
			Maybe<GenericModelType> iterableType = resolvePath(iterablePath);
			if (iterableType.isEmpty())
				return Maybe.empty(iterableType.whyUnsatisfied());
			if (!(iterableType.get() instanceof CollectionType collectionType))
				return Maybe.empty(ParseError.create("For-each iterable is not a collection: " + iterablePath));

			ForEach node = ForEach.T.create();
			node.setIterable(List.of());
			node.setVariable(dev.hiconic.template.impl.parser.DefinitionTools.variable(option(tokens, "as:", "item"),
					collectionType.getCollectionElementType().getTypeSignature(), false));
			String index = option(tokens, "index:", null);
			if (index != null) node.setIndexVariable(dev.hiconic.template.impl.parser.DefinitionTools.variable(index, "integer", false));
			elementTypes.put(node, collectionType.getCollectionElementType());
			resolvedPaths.add(iterablePath);
			return Maybe.complete(node);
		}

		@Override
		public Reason enterBlock(TemplateNode owner, String blockProperty, TextRange range) {
			Map<String, GenericModelType> scope = new HashMap<>();
			if (owner instanceof ForEach forEach && "block".equals(blockProperty)) {
				scope.put(dev.hiconic.template.impl.parser.DefinitionTools.name(forEach.getVariable()), elementTypes.get(forEach));
				if (forEach.getIndexVariable() != null)
					scope.put(dev.hiconic.template.impl.parser.DefinitionTools.name(forEach.getIndexVariable()), com.braintribe.model.generic.reflection.SimpleTypes.TYPE_INTEGER);
			}
			scopes.push(scope);
			return null;
		}

		@Override
		public void exitBlock(TemplateNode owner, String blockProperty) {
			scopes.pop();
		}

		protected Maybe<GenericModelType> resolvePath(String path) {
			String[] segments = path.trim().split("\\.");
			GenericModelType type = findVariable(segments[0]);
			if (type == null)
				return Maybe.empty(ParseError.create("Unknown template variable: " + segments[0]));

			for (int i = 1; i < segments.length; i++) {
				if (!(type instanceof EntityType<?> entityType))
					return Maybe.empty(ParseError.create(
							"Cannot access property '" + segments[i] + "' on " + type.getTypeName()));
				Property property = entityType.findProperty(segments[i]);
				if (property == null)
					return Maybe.empty(ParseError.create(
							"Unknown property '" + segments[i] + "' on " + entityType.getTypeSignature()));
				type = property.getType();
			}
			return Maybe.complete(type);
		}

		protected GenericModelType findVariable(String name) {
			for (Map<String, GenericModelType> scope : scopes) {
				GenericModelType type = scope.get(name);
				if (type != null)
					return type;
			}
			return null;
		}

		private static String option(String[] tokens, String option, String defaultValue) {
			for (int i = 2; i + 1 < tokens.length; i++)
				if (option.equals(tokens[i]))
					return tokens[i + 1];
			return defaultValue;
		}
	}

	private static class PipelineResolver extends TypedInputResolver implements TemplateValueExpressionResolver {
		private final StandardOutputExpressionParser outputParser;

		private PipelineResolver() {
			ConfigurableTemplateExpertRegistry registry = new ConfigurableTemplateExpertRegistry();
			StandardTemplateExperts.register(registry);
			outputParser = new StandardOutputExpressionParser(registry, this);
		}

		@Override
		public Maybe<OutputNode> resolveOutput(String expression, TextRange range) {
			return outputParser.parse(expression, range);
		}

		@Override
		public Maybe<ParsedValueExpression> resolveValue(String expression, TextRange range) {
			Maybe<GenericModelType> maybeType = resolvePath(expression);
			if (maybeType.isUnsatisfied())
				return Maybe.empty(maybeType.whyUnsatisfied());
			return Maybe.complete(new ParsedValueExpression(pathDescriptor(expression, maybeType.get()), maybeType.get()));
		}

		private Object pathDescriptor(String path, GenericModelType type) {
			String[] segments = path.trim().split("\\.");
			Variable variable = Variable.T.create();
			variable.setName(segments[0]);
			GenericModelType rootType = findVariable(segments[0]);
			if (rootType != null)
				variable.setTypeSignature(rootType.getTypeSignature());
			if (segments.length == 1)
				return variable;

			PropertyPath propertyPath = PropertyPath.T.create();
			propertyPath.setEntity(variable);
			propertyPath.setPropertyPath(path.substring(segments[0].length() + 1));
			return propertyPath;
		}
	}
}
