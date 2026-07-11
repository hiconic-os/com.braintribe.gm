package dev.hiconic.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Map;

import org.junit.Test;

import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.ResolvedTemplateDefaults;
import dev.hiconic.template.api.TemplateDefaults;
import dev.hiconic.template.impl.AbstractScopedTemplateEvaluationContext;
import dev.hiconic.template.model.core.TemplateEvaluationDefaults;
import dev.hiconic.template.model.core.TemplateNode;

public class ScopedTemplateEvaluationContextTest {
	@Test
	public void nestedRuntimeScopesSeeParentsAndDoNotLeak() {
		TestContext context = new TestContext();
		context.declareVariable("outer", "root");

		context.withVariables(Map.of("parameter", 1), () -> {
			assertEquals("root", context.getVariable("outer"));
			assertEquals(1, context.getVariable("parameter"));
			context.withVariables(Map.of("local", true), () -> {
				assertEquals("root", context.getVariable("outer"));
				assertEquals(1, context.getVariable("parameter"));
				assertEquals(true, context.getVariable("local"));
			});
			assertThrows(IllegalArgumentException.class, () -> context.getVariable("local"));
		});

		assertThrows(IllegalArgumentException.class, () -> context.getVariable("parameter"));
		assertEquals("root", context.getVariable("outer"));
	}

	private static class TestContext extends AbstractScopedTemplateEvaluationContext {
		@Override
		public Object evaluate(ValueDescriptor vd) {
			return vd;
		}

		@Override
		public void evaluate(TemplateNode node) {
		}

		@Override
		public void append(String text) {
		}

		@Override
		public boolean allowsNoEscape() {
			return false;
		}

		@Override
		public TemplateEvaluationDefaults defaults() {
			return TemplateDefaults.standard();
		}

		@Override
		public ResolvedTemplateDefaults resolvedDefaults() {
			return ResolvedTemplateDefaults.of(defaults());
		}
	}
}
