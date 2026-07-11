package dev.hiconic.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.Template;
import dev.hiconic.template.api.TemplateFactories;
import dev.hiconic.template.model.core.SequenceNode;
import dev.hiconic.template.test.model.TemplateTestInput;

public class TemplateApiTest {
	@Test
	public void htmlFactoryParsesTypedTemplateAndEvaluatesToString() {
		Maybe<Template<TemplateTestInput>> maybe = TemplateFactories.html()
				.withRoot(TemplateTestInput.T)
				.parse("Hello ${input.title}! ${input.birthday | format-date \"yyyy-MM-dd\"}");

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
				.parse("${input | no-escape}");

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("<Dirk>", maybe.get().evaluateToString("<Dirk>"));
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
				.parse("${input | format-date \"yyyy-MM-dd HH:mm\" --zone Europe/Berlin}");

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
				.parse("${input | format-number \"#,##0.00\" --locale de-DE}");

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
		String source = "#{declare-instruction greet name:string}"
				+ "<b>${name}</b>"
				+ "#{end}"
				+ "<h1>%{greet \"Dirk\"}%</h1>";

		Maybe<Template<TemplateTestInput>> maybe = TemplateFactories.html()
				.withRoot(TemplateTestInput.T)
				.parse(source);

		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		assertEquals("<h1><b>Dirk</b></h1>", maybe.get().evaluateToString(TemplateTestInput.T.create()));
	}
}
