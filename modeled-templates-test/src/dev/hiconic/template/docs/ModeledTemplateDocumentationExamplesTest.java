package dev.hiconic.template.docs;

import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Locale;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.Template;
import dev.hiconic.template.api.TemplateFactories;
import dev.hiconic.template.api.TemplateFactory;
import dev.hiconic.template.test.model.demo.Conference;

public class ModeledTemplateDocumentationExamplesTest {
	private static final Path RESOURCE_ROOT = Path.of("res", "docs");

	@Test
	public void rendersConferenceAgendaExample() throws Exception {
		String output = render("conference-agenda.html.mt");

		assertContains(output, "<h1>Hiconic Model Templates Summit</h1>");
		assertContains(output, "<h2>Program map</h2>");
		assertContains(output, "data-depth=\"0\">MOD: Reflective Modeling");
		assertContains(output, "data-depth=\"1\">MOD-GM: GM Meta Models");
		assertContains(output, "data-depth=\"1\">DEL-DOCS: Executable Documentation");
		assertContains(output, "Models as an Exo-Type System");
		assertContains(output, "11:00 · 50 min ·");
		assertContains(output, "Opening keynote");
		assertContains(output, "Featured session");
		assertContains(output, "Track session");
		assertContains(output, "<li>SafeOutput</li>");
	}

	@Test
	public void rendersSpeakerCatalogExampleWithEscapedHtmlOutput() throws Exception {
		String output = render("speaker-catalog.html.mt");

		assertContains(output, "Speakers for Hiconic Model Templates Summit");
		assertContains(output, "Ada Krämer");
		assertContains(output, "Recognized field expert");
		assertContains(output, "Community contributor");
		assertContains(output, "<li>developer-experience</li>");
	}

	@Test
	public void rendersTicketTextExampleWithNumberAndDateDefaults() throws Exception {
		String output = render("tickets.txt.mt");

		assertContains(output, "Tickets for Hiconic Model Templates Summit");
		assertContains(output, "Published: 2026-07-15");
		assertContains(output, "Community: 99.00 EUR");
		assertContains(output, "Professional: 249.00 EUR");
		assertContains(output, "includes workshops");
		assertContains(output, "conference talks only");
	}

	private static String render(String resource) throws Exception {
		TemplateFactory factory = TemplateFactories.html()
				.defaultLocale(Locale.US)
				.defaultZone(ZoneId.of("Europe/Berlin"));
		Maybe<Template<Conference>> maybe = factory.withRoot(Conference.T).parse(Files.readString(
				RESOURCE_ROOT.resolve(resource), StandardCharsets.UTF_8));
		assertTrue(maybe.isSatisfied() ? "" : maybe.whyUnsatisfied().stringify(), maybe.isSatisfied());
		return maybe.get().evaluateToString(ConferenceDemoData.createConference());
	}

	private static void assertContains(String output, String expected) {
		assertTrue("Expected output to contain:\n" + expected + "\n\nActual output:\n" + output,
				output.contains(expected));
	}
}
