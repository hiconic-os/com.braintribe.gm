package dev.hiconic.template.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dev.hiconic.template.impl.parser.TemplateTypeNameResolver;

public class TemplateTypeNameResolverTest {
	@Test
	public void derivesStableLowerKebabAliases() {
		assertEquals("add", TemplateTypeNameResolver.toLowerKebabCase("Add"));
		assertEquals("for-each", TemplateTypeNameResolver.toLowerKebabCase("ForEach"));
		assertEquals("java-literal-escape", TemplateTypeNameResolver.toLowerKebabCase("JavaLiteralEscape"));
		assertEquals("url-component-escape", TemplateTypeNameResolver.toLowerKebabCase("URLComponentEscape"));
	}

	@Test
	public void recognizesOnlyCanonicalLowerKebabNamesAsAliases() {
		assertTrue(TemplateTypeNameResolver.isLowerKebabCase("for-each"));
		assertTrue(TemplateTypeNameResolver.isLowerKebabCase("add"));
		assertFalse(TemplateTypeNameResolver.isLowerKebabCase("ForEach"));
		assertFalse(TemplateTypeNameResolver.isLowerKebabCase("for_each"));
		assertFalse(TemplateTypeNameResolver.isLowerKebabCase("dev.example.ForEach"));
	}
}
