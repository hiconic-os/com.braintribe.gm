package dev.hiconic.template.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.braintribe.model.generic.reflection.SimpleTypes;

import dev.hiconic.template.impl.parser.TemplateValidationScope;

public class TemplateValidationScopeTest {
	@Test
	public void nestedScopesSeeParentsAndDiscardTheirLocalsOnExit() {
		TemplateValidationScope scope =
				new TemplateValidationScope(Map.of("input", SimpleTypes.TYPE_STRING));
		scope.enter();
		scope.declare("parameter", SimpleTypes.TYPE_INTEGER);
		scope.enter();
		scope.declare("local", SimpleTypes.TYPE_BOOLEAN);

		assertEquals(SimpleTypes.TYPE_STRING, scope.resolve("input").get());
		assertEquals(SimpleTypes.TYPE_INTEGER, scope.resolve("parameter").get());
		assertEquals(SimpleTypes.TYPE_BOOLEAN, scope.resolve("local").get());

		scope.exit();
		assertTrue(scope.resolve("local").isUnsatisfied());
		assertEquals(SimpleTypes.TYPE_INTEGER, scope.resolve("parameter").get());

		scope.exit();
		assertTrue(scope.resolve("parameter").isUnsatisfied());
		assertEquals(SimpleTypes.TYPE_STRING, scope.resolve("input").get());
	}

	@Test
	public void duplicateDeclarationIsRejectedAcrossVisibleScopesButAllowedAfterExit() {
		TemplateValidationScope scope = new TemplateValidationScope(Map.of());
		assertTrue(scope.declare("value", SimpleTypes.TYPE_STRING).isSatisfied());
		assertTrue(scope.declare("value", SimpleTypes.TYPE_INTEGER).isUnsatisfied());

		scope.enter();
		assertTrue(scope.declare("value", SimpleTypes.TYPE_INTEGER).isUnsatisfied());
		scope.exit();

		scope.enter();
		assertTrue(scope.declare("other", SimpleTypes.TYPE_INTEGER).isSatisfied());
		scope.exit();
		scope.enter();
		assertTrue(scope.declare("other", SimpleTypes.TYPE_BOOLEAN).isSatisfied());
	}
}
