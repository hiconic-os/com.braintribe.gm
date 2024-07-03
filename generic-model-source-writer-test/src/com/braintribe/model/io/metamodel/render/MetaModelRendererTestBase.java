// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.io.metamodel.render;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;

import org.junit.Before;

import com.braintribe.model.io.metamodel.testbase.MetaModelBuilder;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.utils.StringTools;

public abstract class MetaModelRendererTestBase {

	protected MetaModelRenderer metaModelRenderer;

	protected String currentCode;

	private final SourceWriterContext context = new SourceWriterContext();

	@Before
	public void setUp() throws Exception {
		GmMetaModel gmMetaModel = new MetaModelBuilder().buildMetaModel();
		context.modelOracle = new BasicModelOracle(gmMetaModel);
		metaModelRenderer = new MetaModelRenderer(context);
	}

	protected void renderEntityType(GmEntityType gmEntityType) {
		currentCode = metaModelRenderer.renderEntityType(gmEntityType).sourceCode;
	}

	protected void renderEnumType(GmEnumType gmEnumType) {
		currentCode = metaModelRenderer.renderEnumType(gmEnumType).sourceCode;
	}

	protected void assertHasImportedAnnotation(Class<? extends Annotation> annotation) {
		assertContainsSubstring("@" + annotation.getSimpleName());
		assertHasImport(annotation);
	}

	protected void assertHasImportedAnnotation(Class<? extends Annotation> annotation, String value) {
		assertContainsSubstring("@" + annotation.getSimpleName() + "(\"" + value + "\")");
		assertHasImport(annotation);
	}

	protected void assertHasNoAnnotation(Class<? extends Annotation> annotation) {
		assertThat(currentCode.contains(annotation.getSimpleName())).as("Annotation should not be present: " + annotation).isFalse();
	}

	protected void assertHasImport(Class<?> clazz) {
		assertHasImport(clazz.getName());
	}

	protected void assertHasImport(String className) {
		assertContainsSubstring("import " + className);
	}

	protected void assertHasProperty(String capitalizedName, String type) {
		String name = StringTools.uncapitalize(capitalizedName);

		assertContainsSubstring("String " + name + " = \"" + name + "\";");
		assertContainsSubstring("void set" + capitalizedName + "(" + type + " " + name + ");");
		assertContainsSubstring(type + " get" + capitalizedName + "()");
	}

	protected void assertHasEscapedProperty(String capitalizedName, String type, String literalName, String setterParamName) {
		String name = StringTools.uncapitalize(capitalizedName);

		assertContainsSubstring("String " + literalName + " = \"" + name + "\";");
		assertContainsSubstring("void set" + capitalizedName + "(" + type + " " + setterParamName + ");");
		assertContainsSubstring(type + " get" + capitalizedName + "()");
	}

	protected void assertDoesntHaveProperty(String name, String type) {
		assertNotContains("void set" + name + "(" + type + " value);");
		assertNotContains(type + " get" + name + "()");
	}

	protected void assertContainsSubstrings(String... strings) {
		for (String s : strings) {
			assertContainsSubstring(s);
		}
	}

	protected void assertContainsSubstring(String s) {
		assertThat(currentCode).contains(s);
	}

	protected void assertNotContains(String s) {
		assertThat(currentCode).doesNotContain(s);
	}

	protected GmEntityType getTypeBySignature(String signature) {
		return context.modelOracle.getEntityTypeOracle(signature).asGmType();
	}

	protected GmEnumType getEnumBySignature(String signature) {
		return context.modelOracle.getEnumTypeOracle(signature).asGmType();
	}

	protected void printCode() {
		System.out.println(currentCode);
	}

}
