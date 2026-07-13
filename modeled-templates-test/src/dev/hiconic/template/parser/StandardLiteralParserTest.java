package dev.hiconic.template.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.SimpleTypes;

import dev.hiconic.template.impl.parser.ParsedLiteral;
import dev.hiconic.template.impl.parser.StandardLiteralParser;
import dev.hiconic.template.test.model.TestColor;

public class StandardLiteralParserTest {
	private final StandardLiteralParser parser = new StandardLiteralParser();

	@Test
	public void preservesExplicitLongAndFloatNumberTypes() {
		assertEquals(SimpleTypes.TYPE_LONG, parser.parse("long:42").get().type());
		assertEquals(42L, parser.parse("long:42").get().value());
		assertEquals(SimpleTypes.TYPE_FLOAT, parser.parse("float:1.5").get().type());
		assertEquals(1.5f, parser.parse("float:1.5").get().value());
		assertEquals(SimpleTypes.TYPE_LONG, parser.parse("42", SimpleTypes.TYPE_LONG).get().type());
		assertEquals(SimpleTypes.TYPE_FLOAT, parser.parse("1.5", SimpleTypes.TYPE_FLOAT).get().type());
	}

	@Test
	public void decodesAllStringEscapeForms() {
		Maybe<ParsedLiteral> maybe = parser.parse("string:\"quote=\\\" slash=\\\\ / tab=\\t newline=\\n unicode=\\u20ac\"");

		assertTrue(maybe.isSatisfied());
		assertEquals("quote=\" slash=\\ / tab=\t newline=\n unicode=€", maybe.get().value());
	}

	@Test
	public void rejectsUnknownAndIncompleteStringEscapes() {
		assertTrue(parser.parse("string:\"\\x\"").isUnsatisfied());
		assertTrue(parser.parse("string:\"\\u12\"").isUnsatisfied());
		assertTrue(parser.parse("string:\"trailing\\\"").isUnsatisfied());
	}

	@Test
	public void parsesEnumLiteralWithItsReflectedType() {
		String literal = TestColor.T.getTypeSignature() + "::green";

		Maybe<ParsedLiteral> maybe = parser.parse(literal);

		assertTrue(maybe.isSatisfied());
		assertEquals(TestColor.green, maybe.get().value());
		assertEquals(TestColor.T.getTypeSignature(), maybe.get().type().getTypeSignature());
	}

	@Test
	public void infersEnumTypeFromBindingTarget() {
		Maybe<ParsedLiteral> maybe = parser.parse("::blue", TestColor.T);

		assertTrue(maybe.isSatisfied());
		assertEquals(TestColor.blue, maybe.get().value());
	}

	@Test
	public void rejectsInferredEnumWithoutBindingTarget() {
		assertTrue(parser.parse("::blue").isUnsatisfied());
	}

	@Test
	public void rejectsUnknownEnumConstant() {
		String literal = TestColor.T.getTypeSignature() + "::purple";

		assertTrue(parser.parse(literal).isUnsatisfied());
	}
}
