package dev.hiconic.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.ReasonException;
import com.braintribe.gm._ModeledTemplatesTestModel_;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;

import dev.hiconic.template.api.Template;
import dev.hiconic.template.api.TemplateFactories;
import dev.hiconic.template.api.TemplateFactory;
import dev.hiconic.template.model.core.SequenceNode;
import dev.hiconic.template.model.evaluation.NullPathElement;
import dev.hiconic.template.model.evaluation.PathEvaluationError;
import dev.hiconic.template.model.parse.TemplateParseError;
import dev.hiconic.template.test.model.TestAddress;
import dev.hiconic.template.test.model.TestPerson;
import dev.hiconic.template.test.model.TemplateTestInput;

public class TemplateApiTest {
	@Test
	public void variableDeclarationIsStaticallyVisibleAndExecutesAtRuntime() {
		Maybe<Template<String>> maybe = TemplateFactories.html()
				.withRoot(String.class)
				.parse("%(var greeting string value: \"Hello\")${greeting}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("Hello", maybe.get().evaluateToString("ignored"));
	}

	@Test
	public void variableDeclarationRejectsForwardReferenceAndInstructionSigil() {
		Maybe<Template<String>> forward = TemplateFactories.html()
				.withRoot(String.class)
				.parse("${greeting}%(var greeting string value: \"Hello\")");
		Maybe<Template<String>> percent = TemplateFactories.html()
				.withRoot(String.class)
				.parse("%(gt 1 0)");

		assertTrue(forward.isUnsatisfied());
		assertTrue(percent.isUnsatisfied());
	}

	@Test
	public void variablesFollowLexicalRuntimeBlockScopes() {
		String source = "%(if true)%(var local string value: \"a\")${local}%(end)"
				+ "%(if true)%(var local string value: \"b\")${local}%(end)";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("ab", maybe.get().evaluateToString("ignored"));
	}

	@Test
	public void setMutatesNearestVisibleVariableWithStaticTypeChecking() {
		String source = "%(var greeting string value: \"Hello\")${greeting}"
				+ "%(set greeting \"World\")${greeting}";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);
		Maybe<Template<String>> wrongType = TemplateFactories.html().withRoot(String.class)
				.parse("%(var greeting string value: \"Hello\")%(set greeting 42)");
		Maybe<Template<String>> unknown = TemplateFactories.html().withRoot(String.class)
				.parse("%(set missing \"value\")");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("HelloWorld", maybe.get().evaluateToString("ignored"));
		assertTrue(wrongType.isUnsatisfied());
		assertTrue(unknown.isUnsatisfied());
	}

	@Test
	public void infersVariableTypesAndSetAcceptsNaturalExpressions() {
		String source = "%(var greeting value: input.name)"
				+ "%(set greeting (concat greeting \"!\"))${greeting}";
		Maybe<Template<TestPerson>> maybe = TemplateFactories.html().withRoot(TestPerson.T).parse(source);
		TestPerson input = TestPerson.T.create();
		input.setName("Ada");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("Ada!", maybe.get().evaluateToString(input));
	}

	@Test
	public void variablesCannotShadowVisibleBindingsButMayRepeatInDisjointBlocks() {
		Maybe<Template<String>> shadow = TemplateFactories.html().withRoot(String.class)
				.parse("%(var value string value: \"a\")%(if true)%(var value string value: \"b\")%(end)");
		Maybe<Template<String>> disjoint = TemplateFactories.html().withRoot(String.class)
				.parse("%(if true)%(var value string value: \"a\")%(end)"
						+ "%(if true)%(var value string value: \"b\")%(end)");

		assertTrue(shadow.isUnsatisfied());
		assertTrue(disjoint.isSatisfied() ? "" : disjoint.whyUnsatisfied().stringify(), disjoint.isSatisfied());
	}

	@Test
	public void setWritesTypedPropertyPathsButReadonlyRootsRemainProtected() {
		String source = "%(var person value: input)%(set person.address.city \"Vienna\")${person.address.city}";
		Maybe<Template<TestPerson>> maybe = TemplateFactories.html().withRoot(TestPerson.T).parse(source);
		Maybe<Template<TestPerson>> readonly = TemplateFactories.html().withRoot(TestPerson.T)
				.parse("%(set input.name \"changed\")");
		TestPerson input = TestPerson.T.create();
		TestAddress address = TestAddress.T.create();
		input.setAddress(address);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("Vienna", maybe.get().evaluateToString(input));
		assertEquals("Vienna", address.getCity());
		assertTrue(readonly.isUnsatisfied());
	}

	@Test
	public void strictAndOptionalRValuePathsHaveControlledNullSemantics() {
		Maybe<Template<TestPerson>> strict = TemplateFactories.html().withRoot(TestPerson.T)
				.parse("${input.address.city}");
		Maybe<Template<TestPerson>> optional = TemplateFactories.html().withRoot(TestPerson.T)
				.parse("%(var city string value: \"fallback\")%(set city input.address?.city)");
		TestPerson input = TestPerson.T.create();

		assertTrue(strict.isSatisfied() ? "" : strict.whyUnsatisfied().stringify(), strict.isSatisfied());
		assertTrue(optional.isSatisfied() ? "" : optional.whyUnsatisfied().stringify(), optional.isSatisfied());
		try {
			strict.get().evaluateToString(input);
			throw new AssertionError("Expected a modeled null-path reason");
		} catch (ReasonException e) {
			assertTrue(e.getReason() instanceof NullPathElement);
			NullPathElement nullPath = (NullPathElement) e.getReason();
			assertEquals("city", nullPath.getSegment());
			assertEquals("input.address.city", nullPath.getPath());
			assertNotNull(nullPath.getRange());
			assertEquals(16, nullPath.getRange().getStart().getOffset());
		}
		assertEquals("", optional.get().evaluateToString(input));
	}

	@Test
	public void readsWritesAndOptionallyNavigatesListAndMapAccesses() {
		String declarations = "%(var values list<string> value: list<string>[\"a\" \"b\"])"
				+ "%(var keyed map<string,string> value: map<string,string>{\"x\": \"one\"})"
				+ "%(var index integer value: 1)";
		Maybe<Template<String>> read = TemplateFactories.html().withRoot(String.class)
				.parse(declarations + "${values[index]}:${keyed[\"x\"]}");
		Maybe<Template<String>> write = TemplateFactories.html().withRoot(String.class)
				.parse(declarations + "%(set values[1] \"c\")%(set keyed[\"x\"] \"two\")"
						+ "${values[1]}:${keyed[\"x\"]}");
		Maybe<Template<String>> optional = TemplateFactories.html().withRoot(String.class)
				.parse(declarations + "before${values?[9]}after");
		Maybe<Template<String>> optionalMissingKey = TemplateFactories.html().withRoot(String.class)
				.parse(declarations + "before${keyed?[\"missing\"]}after");
		Maybe<Template<String>> presentNullValue = TemplateFactories.html().withRoot(String.class)
				.parse("%(var keyed map<string,string> value: map<string,string>{\"x\": null})"
						+ "before${keyed[\"x\"]}after");
		Maybe<Template<String>> wrongIndex = TemplateFactories.html().withRoot(String.class)
				.parse(declarations + "${values[\"x\"]}");
		Maybe<Template<String>> optionalWrite = TemplateFactories.html().withRoot(String.class)
				.parse(declarations + "%(set values?[9] \"x\")");
		String strictSource = declarations + "${values[9]}";
		Maybe<Template<String>> strict = TemplateFactories.html().withRoot(String.class).parse(strictSource);
		String strictMapSource = declarations + "${keyed[\"missing\"]}";
		Maybe<Template<String>> strictMap = TemplateFactories.html().withRoot(String.class).parse(strictMapSource);

		assertTrue(read.isSatisfied() ? "" : read.whyUnsatisfied().stringify(), read.isSatisfied());
		assertTrue(write.isSatisfied() ? "" : write.whyUnsatisfied().stringify(), write.isSatisfied());
		assertTrue(optional.isSatisfied() ? "" : optional.whyUnsatisfied().stringify(), optional.isSatisfied());
		assertTrue(optionalMissingKey.isSatisfied() ? "" : optionalMissingKey.whyUnsatisfied().stringify(),
				optionalMissingKey.isSatisfied());
		assertTrue(presentNullValue.isSatisfied() ? "" : presentNullValue.whyUnsatisfied().stringify(),
				presentNullValue.isSatisfied());
		assertEquals("b:one", read.get().evaluateToString("ignored"));
		assertEquals("c:two", write.get().evaluateToString("ignored"));
		assertEquals("beforeafter", optional.get().evaluateToString("ignored"));
		assertEquals("beforeafter", optionalMissingKey.get().evaluateToString("ignored"));
		assertEquals("beforeafter", presentNullValue.get().evaluateToString("ignored"));
		assertTrue(wrongIndex.isUnsatisfied());
		assertTrue(optionalWrite.isUnsatisfied());
		assertTrue(strict.isSatisfied() ? "" : strict.whyUnsatisfied().stringify(), strict.isSatisfied());
		assertTrue(strictMap.isSatisfied() ? "" : strictMap.whyUnsatisfied().stringify(), strictMap.isSatisfied());
		try {
			strict.get().evaluateToString("ignored");
			throw new AssertionError("Expected a modeled list-bounds reason");
		} catch (ReasonException e) {
			assertTrue(e.getReason() instanceof PathEvaluationError);
			PathEvaluationError error = (PathEvaluationError) e.getReason();
			assertEquals("values[9]", error.getPath());
			assertEquals("[9]", error.getSegment());
			assertEquals(strictSource.lastIndexOf("[9]"), error.getRange().getStart().getOffset());
			assertEquals(strictSource.lastIndexOf("[9]") + 3, error.getRange().getEnd().getOffset());
		}
		try {
			strictMap.get().evaluateToString("ignored");
			throw new AssertionError("Expected a modeled missing-map-key reason");
		} catch (ReasonException e) {
			assertTrue(e.getReason() instanceof PathEvaluationError);
			PathEvaluationError error = (PathEvaluationError) e.getReason();
			assertEquals("keyed[\"missing\"]", error.getPath());
			assertEquals("[\"missing\"]", error.getSegment());
			assertEquals(strictMapSource.lastIndexOf("[\"missing\"]"), error.getRange().getStart().getOffset());
		}
	}

	@Test
	public void collectionExpressionsMaterializeRecursivelyAndPreserveKinds() {
		String source = "%(var values list<string> value: [input \"b\"])"
				+ "%(var keyed map<string,string> value: map<string,string>{input: (concat input \"!\")})"
				+ "${values[0]}:${values[1]}:${keyed[input]}:"
				+ "%(var unique set<string> value: set<string>[input input \"b\"])"
				+ "%(for-each unique item)${item}%(end)"
				+ ":${(concat list<object>[list<string>[input]])}";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("x:b:x!:xb:[x]", maybe.get().evaluateToString("x"));
	}

	@Test
	public void mapForEachAndVariadicSetsKeepTheirReflectedTypes() {
		String source = "%(var keyed map<string,string> value: map<string,string>{\"a\": \"one\" \"b\": \"two\"})"
				+ "%(for-each-entry keyed key: key value: value)${key}:${value};%(end)"
				+ "|%(for-each-entry keyed key: onlyKey)${onlyKey}%(end)"
				+ "|%(for-each-entry keyed value: onlyValue)${onlyValue}%(end)|"
				+ "%(declare-instruction unique {values set<string>})"
				+ "%(for-each values value)${value}%(end)%(end)"
				+ "%(unique input input \"z\")";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);
		Maybe<Template<String>> duplicateMap = TemplateFactories.html().withRoot(String.class)
				.parse("%(var keyed map<string,string> value: map<string,string>{\"x\": \"one\" \"x\": \"two\"})");
		Maybe<Template<String>> mapThroughOrdinaryForEach = TemplateFactories.html().withRoot(String.class)
				.parse("%(var keyed map<string,string> value: map<string,string>{\"x\": \"one\"})"
						+ "%(for-each keyed entry)${entry}%(end)");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("a:one;b:two;|ab|onetwo|xz", maybe.get().evaluateToString("x"));
		assertTrue(duplicateMap.isUnsatisfied());
		assertTrue(mapThroughOrdinaryForEach.isUnsatisfied());
	}

	@Test
	public void collectionMutationStatementsAreTypedAndEvaluateNaturally() {
		String source = "%(var values list<string> value: [\"b\"])"
				+ "%(insert values 0 \"a\")"
				+ "%(append values (concat \"c\"))"
				+ "%(remove values \"b\")"
				+ "%(for-each values value)${value}%(end)"
				+ "%(var tags set<string> value: set<string>[\"x\"])"
				+ "%(add tags \"y\")%(remove tags \"x\")"
				+ "%(for-each tags tag)${tag}%(end)"
				+ "%(var lookup map<string,string> value: map<string,string>{})"
				+ "%(put lookup \"a\" \"A\")%(put lookup \"b\" \"B\")%(remove lookup \"a\")"
				+ "${lookup[\"b\"]}";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("acyB", maybe.get().evaluateToString("ignored"));

		Maybe<Template<TestPerson>> pathMutation = TemplateFactories.html().withRoot(TestPerson.T)
				.parse("%(var person value: input)%(append person.tags \"b\")"
						+ "%(for-each person.tags tag)${tag}%(end)");
		TestPerson person = TestPerson.T.create();
		person.setTags(new ArrayList<>(java.util.List.of("a")));
		assertTrue(pathMutation.isSatisfied() ? "" : pathMutation.whyUnsatisfied().stringify(), pathMutation.isSatisfied());
		assertEquals("ab", pathMutation.get().evaluateToString(person));
	}

	@Test
	public void collectionMutationsRejectWrongKindsAndValuesDuringParsing() {
		Maybe<Template<String>> wrongKind = TemplateFactories.html().withRoot(String.class)
				.parse("%(var values list<string> value: [])%(add values \"x\")");
		Maybe<Template<String>> wrongValue = TemplateFactories.html().withRoot(String.class)
				.parse("%(var values list<string> value: [])%(append values 42)");

		assertTrue(wrongKind.isUnsatisfied());
		assertTrue(wrongKind.whyUnsatisfied().stringify(),
				wrongKind.whyUnsatisfied().stringify().contains("target has incompatible type list<string>"));
		assertTrue(wrongValue.isUnsatisfied());
		assertTrue(wrongValue.whyUnsatisfied().stringify(),
				wrongValue.whyUnsatisfied().stringify().contains("expects string"));
	}

	@Test
	public void constraintErrorsPointAtTheExactNestedArgument() {
		String source = "%(var fixture\n  value: (ConstraintFixture \"A\" 5))";
		Maybe<Template<TestPerson>> parsed = metadataFactory().withRoot(TestPerson.T).parse(source);

		assertTrue(parsed.isUnsatisfied());
		TemplateParseError error = findParseError(parsed.whyUnsatisfied(), "Var.value.code");
		assertNotNull(error);
		assertEquals(source.indexOf("\"A\""), error.getRange().getStart().getOffset());
		assertEquals(source.indexOf("\"A\"") + 3, error.getRange().getEnd().getOffset());
		assertEquals(2, error.getRange().getStart().getLine());
	}

	@Test
	public void statementInstructionsStripOnlyStatementOnlyLinesByDefault() {
		String lines = "%(var value string value: \"a\")\nbefore\n  %(set value \"b\")\nafter:${value}";
		String inline = "%(var value string value: \"a\")before %(set value \"b\") after:${value}";
		Maybe<Template<String>> lineTemplate = TemplateFactories.html().withRoot(String.class).parse(lines);
		Maybe<Template<String>> inlineTemplate = TemplateFactories.html().withRoot(String.class).parse(inline);

		assertTrue(lineTemplate.isSatisfied() ? "" : lineTemplate.whyUnsatisfied().stringify(), lineTemplate.isSatisfied());
		assertTrue(inlineTemplate.isSatisfied() ? "" : inlineTemplate.whyUnsatisfied().stringify(), inlineTemplate.isSatisfied());
		assertEquals("beforeafter:b", lineTemplate.get().evaluateToString("ignored"));
		assertEquals("before  after:b", inlineTemplate.get().evaluateToString("ignored"));
	}

	@Test
	public void bindsInferredEntityAndMapLiteralsAndAppliesWhitespacePolicy() {
		String source = "%(declare-instruction use-map {values map<string,integer>})mapped%(end)"
				+ "before\n%(use-map {\"one\": 1} "
				+ "whitespace: {before: ::trimLine after: ::trimLine})\nafter";
		Maybe<Template<String>> maybe = TemplateFactories.html()
				.withRoot(String.class)
				.parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("beforemappedafter", maybe.get().evaluateToString("ignored"));
	}

	@Test
	public void supportsInferredAndExplicitCollectionLiteralsWithControlledNesting() {
		String source = "%(declare-instruction list-use {values list<string>})L%(end)"
				+ "%(declare-instruction set-use {values set<string>})S%(end)"
				+ "%(declare-instruction object-use {value object})O%(end)"
				+ "%(list-use [\"a\" \"b\"])"
				+ "%(set-use [\"a\" \"b\"])"
				+ "%(object-use set<string>[\"a\" \"b\"])"
				+ "%(object-use [list<string>[\"nested\"]])";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);
		String forbidden = "%(declare-instruction nested {values list<list<string>>})x%(end)"
				+ "%(nested [[\"a\"]])";
		Maybe<Template<String>> nested = TemplateFactories.html().withRoot(String.class).parse(forbidden);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("LSOO", maybe.get().evaluateToString("ignored"));
		assertTrue(nested.isUnsatisfied());
	}

	@Test
	public void validatesKnownDirectValuesAgainstPropertyMetadataAfterCompletion() {
		TemplateFactory factory = metadataFactory();
		Maybe<Template<TestPerson>> valid = factory.withRoot(TestPerson.T)
				.parse("%(var fixture value: (ConstraintFixture \"AB\" 5 dynamic: input.name))");
		String[] invalid = {
				"%(var fixture value: (ConstraintFixture score: 5))",
				"%(var fixture value: (ConstraintFixture \"Ab\" 5))",
				"%(var fixture value: (ConstraintFixture \"A\" 5))",
				"%(var fixture value: (ConstraintFixture \"ABCDE\" 5))",
				"%(var fixture value: (ConstraintFixture \"AB\" 0))",
				"%(var fixture value: (ConstraintFixture \"AB\" 10))"
		};

		assertTrue(valid.isSatisfied() ? "" : valid.whyUnsatisfied().stringify(), valid.isSatisfied());
		for (String source : invalid) {
			Maybe<Template<TestPerson>> parsed = factory.withRoot(TestPerson.T).parse(source);
			assertTrue("Expected metadata violation for " + source, parsed.isUnsatisfied());
			String reason = parsed.whyUnsatisfied().stringify();
			assertTrue(reason, reason.contains("Constraint violation at Var.value."));
		}
	}

	@Test
	public void nullIsAnOrdinaryLiteralControlledByMandatoryAndPropertyNullability() {
		TemplateFactory factory = metadataFactory();
		Maybe<Template<String>> variableAndCollection = factory.withRoot(String.class)
				.parse("%(var value string value: null)%(var values list<string> value: list<string>[null \"a\"])");
		Maybe<Template<String>> nullOutput = factory.withRoot(String.class).parse("before${null}after");
		Maybe<Template<String>> nullableProperty = factory.withRoot(String.class)
				.parse("%(var fixture value: (ConstraintFixture \"AB\" null))");
		Maybe<Template<String>> mandatory = factory.withRoot(String.class)
				.parse("%(var fixture value: (ConstraintFixture null 5))");

		assertTrue(variableAndCollection.isSatisfied() ? "" : variableAndCollection.whyUnsatisfied().stringify(),
				variableAndCollection.isSatisfied());
		assertTrue(nullOutput.isSatisfied() ? "" : nullOutput.whyUnsatisfied().stringify(), nullOutput.isSatisfied());
		assertEquals("beforeafter", nullOutput.get().evaluateToString("input"));
		assertTrue(nullableProperty.isSatisfied() ? "" : nullableProperty.whyUnsatisfied().stringify(),
				nullableProperty.isSatisfied());
		assertTrue(mandatory.isUnsatisfied());
	}

	@Test
	public void htmlFactoryParsesTypedTemplateAndEvaluatesToString() {
		Maybe<Template<TemplateTestInput>> maybe = TemplateFactories.html()
				.withRoot(TemplateTestInput.T)
				.parse("Hello ${input.title}! ${input.birthday | (format-date \"yyyy-MM-dd\")}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		Template<TemplateTestInput> template = maybe.get();
		assertTrue(template.rootNode() instanceof SequenceNode);

		TemplateTestInput input = TemplateTestInput.T.create();
		input.setTitle("<Dirk>");
		input.setBirthday(Date.from(Instant.parse("2026-07-10T00:00:00Z")));

		assertEquals("Hello &lt;Dirk&gt;! 2026-07-10", template.evaluateToString(input));
	}

	@Test
	public void classLiteralRootInfersModelTypeAndDefaultEscapes() {
		Maybe<Template<String>> maybe = TemplateFactories.html()
				.withRoot(String.class)
				.parse("${input}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("&lt;Dirk&gt;", maybe.get().evaluateToString("<Dirk>"));
	}

	@Test
	public void noEscapeIsAllowedByDefaultButStillExplicitInTheModel() {
		Maybe<Template<String>> maybe = TemplateFactories.html()
				.withRoot(String.class)
				.parse("${input | (no-escape)}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("<Dirk>", maybe.get().evaluateToString("<Dirk>"));
	}

	@Test
	public void pipelineFeedsTheOrdinaryPositionalAndVariadicBinder() {
		Maybe<Template<String>> maybe = TemplateFactories.html()
				.withRoot(String.class)
				.parse("${1 | (add 2)}:${input | (concat \"b\" \"c\")}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("3:abc", maybe.get().evaluateToString("a"));
	}

	@Test
	public void castIsAnOrdinaryValueDescriptorPipelineStage() {
		Maybe<Template<String>> maybe = TemplateFactories.html()
				.withRoot(String.class)
				.parse("${input | (cast string)}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("value", maybe.get().evaluateToString("value"));
	}

	@Test
	public void reflectedTypeDescriptorsDistinguishDeclaredAndRuntimeTypes() {
		String source = "%(var value object value: input)"
				+ "%(if (assignable-to (declared-type-of value) object))D%(end)"
				+ "%(if (eq (type-of value) (type string)))R%(end)"
				+ "%(if (is value string))I%(end)"
				+ "%(if (assignable-to (type-of value) object))A%(end)"
				+ "%(if (ne (type-of value) (T integer)))N%(end)"
				+ "%(var values list<string> value: [input])"
				+ "%(if (eq (type-of values) (type list<string>)))C%(end)"
				+ "%(var reflected object value: (T string))"
				+ "%(if (eq reflected (type string)))T%(end)";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("DRIANCT", maybe.get().evaluateToString("x"));
	}

	@Test
	public void reflectedRuntimeTypeTreatsNullExplicitly() {
		Maybe<Template<String>> isNull = TemplateFactories.html().withRoot(String.class)
				.parse("%(var value object value: null)%(if (is value string))wrong%(else)false%(end)");
		Maybe<Template<String>> typeOfNull = TemplateFactories.html().withRoot(String.class)
				.parse("%(var value object value: null)"
						+ "%(if (eq (type-of value) (type string)))wrong%(end)");

		assertTrue(isNull.isSatisfied() ? "" : isNull.whyUnsatisfied().stringify(), isNull.isSatisfied());
		assertTrue(typeOfNull.isSatisfied() ? "" : typeOfNull.whyUnsatisfied().stringify(), typeOfNull.isSatisfied());
		assertEquals("false", isNull.get().evaluateToString("ignored"));
		try {
			typeOfNull.get().evaluateToString("ignored");
			throw new AssertionError("Expected type-of null to fail");
		} catch (ReasonException e) {
			assertTrue(e.getReason().stringify(), e.getReason().stringify().contains("no concrete runtime type"));
		}
	}

	@Test
	public void explicitTypeReferenceRejectsUnknownType() {
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class)
				.parse("%(var reflected object value: (type no.such.Type))");

		assertTrue(maybe.isUnsatisfied());
		assertTrue(maybe.whyUnsatisfied().stringify(),
				maybe.whyUnsatisfied().stringify().contains("Unknown type reference: no.such.Type"));
	}

	@Test
	public void explicitTypeReferenceConsumesItsCompleteScalarRemainder() {
		String source = "%(var strings object value: (type list< string >))"
				+ "%(var mapping object value: (T map< string, integer >))"
				+ "%(if (eq strings (type list<string>)))L%(end)"
				+ "%(if (eq mapping (T map<string,integer>)))M%(end)";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("LM", maybe.get().evaluateToString("ignored"));
	}

	@Test
	public void defaultDateFormattingUsesConfiguredZone() {
		Date instant = Date.from(Instant.parse("2026-07-09T23:30:00Z"));

		Maybe<Template<Date>> utc = TemplateFactories.html()
				.defaultZone(ZoneId.of("UTC"))
				.withRoot(Date.class)
				.parse("${input}");
		Maybe<Template<Date>> berlin = TemplateFactories.html()
				.defaultZone(ZoneId.of("Europe/Berlin"))
				.withRoot(Date.class)
				.parse("${input}");

		assertTrue(utc.isSatisfied() ? "" : utc.whyUnsatisfied().stringify(), utc.isSatisfied());
		assertTrue(berlin.isSatisfied() ? "" : berlin.whyUnsatisfied().stringify(), berlin.isSatisfied());
		assertEquals("2026-07-09", utc.get().evaluateToString(instant));
		assertEquals("2026-07-10", berlin.get().evaluateToString(instant));
	}

	@Test
	public void explicitDateZoneOverridesConfiguredDefaultZone() {
		Date instant = Date.from(Instant.parse("2026-07-09T23:30:00Z"));

		Maybe<Template<Date>> maybe = TemplateFactories.html()
				.defaultZone(ZoneId.of("UTC"))
				.withRoot(Date.class)
				.parse("${input | (format-date \"yyyy-MM-dd HH:mm\" zone: Europe/Berlin)}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("2026-07-10 01:30", maybe.get().evaluateToString(instant));
	}

	@Test
	public void defaultNumberFormattingUsesConfiguredLocaleAndPattern() {
		Maybe<Template<Double>> maybe = TemplateFactories.html()
				.defaultLocale(Locale.GERMANY)
				.defaultNumberFormat("#,##0.00")
				.withRoot(Double.class)
				.parse("${input}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("1.234,50", maybe.get().evaluateToString(1234.5d));
	}

	@Test
	public void explicitNumberFormattingOverridesConfiguredDefaults() {
		Maybe<Template<Double>> maybe = TemplateFactories.html()
				.defaultLocale(Locale.US)
				.defaultNumberFormat("0")
				.withRoot(Double.class)
				.parse("${input | (format-number \"#,##0.00\" locale: de-DE)}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("1.234,50", maybe.get().evaluateToString(1234.5d));
	}

	@Test
	public void parsesFromReader() throws Exception {
		Maybe<Template<String>> maybe = TemplateFactories.html()
				.withRoot(String.class)
				.parse(new StringReader("Hello ${input}"));

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("Hello &lt;Dirk&gt;", maybe.get().evaluateToString("<Dirk>"));
	}

	@Test
	public void evaluatesConcatValueDescriptorFromTemplateExpression() {
		Maybe<Template<TemplateTestInput>> maybe = TemplateFactories.html()
				.withRoot(TemplateTestInput.T)
				.parse("${(concat \"Hello \" input.title \"!\")}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		TemplateTestInput input = TemplateTestInput.T.create();
		input.setTitle("<Dirk>");
		assertEquals("Hello &lt;Dirk&gt;!", maybe.get().evaluateToString(input));
	}

	@Test
	public void declaredInstructionSeesParametersAndCanBeInvokedLater() {
		String source = "%(declare-instruction greet {name string})"
				+ "<b>${name}</b>"
				+ "%(end)"
				+ "<h1>%(greet \"Dirk\")</h1>";

		Maybe<Template<TemplateTestInput>> maybe = TemplateFactories.html()
				.withRoot(TemplateTestInput.T)
				.parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("<h1><b>Dirk</b></h1>", maybe.get().evaluateToString(TemplateTestInput.T.create()));
	}

	@Test
	public void variableAndParameterDeclarationsShareTheSymbolTypePrefix() {
		String source = "%(var empty string)${empty}"
				+ "%(var inferred value: \"inferred\")${inferred}"
				+ "%(var explicit string value: \"explicit\")${explicit}";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);
		Maybe<Template<String>> positionalValue = TemplateFactories.html().withRoot(String.class)
				.parse("%(var invalid \"value\")");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("inferredexplicit", maybe.get().evaluateToString("ignored"));
		assertTrue(positionalValue.isUnsatisfied());
	}

	@Test
	public void declaredInstructionParametersSupportTypedAndInferredDefaults() {
		String source = "%(declare-instruction greet"
				+ " {name string}"
				+ " {punctuation string default: \"!\"}"
				+ " {suffix default: \".\"})"
				+ "${name}${punctuation}${suffix}%(end)"
				+ "%(greet \"Hi\")%(greet \"Hello\" \"?\" suffix: \"!\")";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);
		Maybe<Template<String>> contradiction = TemplateFactories.html().withRoot(String.class)
				.parse("%(declare-instruction broken {value string required: true default: \"x\"})%(end)");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("Hi!.Hello?!", maybe.get().evaluateToString("ignored"));
		assertTrue(contradiction.isUnsatisfied());
	}

	@Test
	public void parameterDefaultsPreserveNullAndEvaluateValueDescriptorsAtTheCallSite() {
		String source = "%(declare-instruction defaults"
				+ " {optional string required: false}"
				+ " {nullable string default: null}"
				+ " {dynamic string default: input})"
				+ "before${optional}${nullable}${dynamic}after%(end)"
				+ "%(defaults)";
		Maybe<Template<String>> maybe = TemplateFactories.html().withRoot(String.class).parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("beforecall-siteafter", maybe.get().evaluateToString("call-site"));
	}

	@Test
	public void declaredInstructionCanInvokeItself() {
		String source = "%(declare-instruction countdown {done boolean})"
				+ "%(if done)done%(else)%(countdown true)%(end)"
				+ "%(end)%(countdown false)";

		Maybe<Template<TemplateTestInput>> maybe = TemplateFactories.html()
				.withRoot(TemplateTestInput.T)
				.parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("done", maybe.get().evaluateToString(TemplateTestInput.T.create()));
	}

	@Test
	public void declaredInstructionsCanReferenceEachOtherForward() {
		String source = "%(declare-instruction first {done boolean})"
				+ "%(if done)first%(else)%(second true)%(end)%(end)"
				+ "%(declare-instruction second {done boolean})"
				+ "%(if done)mutual%(else)%(first true)%(end)%(end)"
				+ "%(first false)";

		Maybe<Template<TemplateTestInput>> maybe = TemplateFactories.html()
				.withRoot(TemplateTestInput.T)
				.parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("mutual", maybe.get().evaluateToString(TemplateTestInput.T.create()));
	}

	private static TemplateFactory metadataFactory() {
		GmMetaModel model = GMF.getTypeReflection().getModel(_ModeledTemplatesTestModel_.name).getMetaModel();
		var resolver = CmdResolverImpl.create(new BasicModelOracle(model)).done();
		return TemplateFactories.html().cmdResolver(resolver);
	}

	private static TemplateParseError findParseError(Reason reason, String modelPath) {
		if (reason instanceof TemplateParseError parseError && modelPath.equals(parseError.getModelPath()))
			return parseError;
		for (Reason nested : reason.getReasons()) {
			TemplateParseError found = findParseError(nested, modelPath);
			if (found != null) return found;
		}
		return null;
	}
}
