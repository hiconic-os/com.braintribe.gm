package dev.hiconic.template.parser;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dev.hiconic.template.impl.parser.TemplateDeclarationScope;
import dev.hiconic.template.model.core.decl.DeclareInstruction;

public class TemplateDeclarationScopeTest {
	@Test
	public void declarationsAreLexicalAndMayBeShadowed() {
		TemplateDeclarationScope scope = new TemplateDeclarationScope();
		DeclareInstruction outer = declaration("render");
		scope.declare(outer);
		scope.enter();
		DeclareInstruction inner = declaration("render");
		scope.declare(inner);

		assertSame(inner, scope.resolve("render").get());
		scope.exit();
		assertSame(outer, scope.resolve("render").get());
	}

	@Test
	public void declarationsDoNotLeakFromNestedScopes() {
		TemplateDeclarationScope scope = new TemplateDeclarationScope();
		scope.enter();
		scope.declare(declaration("local"));
		scope.exit();

		assertTrue(scope.resolve("local").isUnsatisfied());
	}

	private static DeclareInstruction declaration(String name) {
		DeclareInstruction declaration = DeclareInstruction.T.create();
		declaration.setName(dev.hiconic.template.impl.parser.DefinitionTools.symbol(name));
		return declaration;
	}
}
