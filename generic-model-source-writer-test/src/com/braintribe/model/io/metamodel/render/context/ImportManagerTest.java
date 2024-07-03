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
package com.braintribe.model.io.metamodel.render.context;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for {@link ImportManager}
 * 
 * @author peter.gazdik
 */
public class ImportManagerTest {

	private ImportManager manager = new ImportManager("my.package");

	@Test
	public void typeForDefaultPackage() throws Exception {
		manager = new ImportManager("");

		manager.useType("DefaultPackageType");
		manager.useType("pckg1.Type");
		manager.useType("pckg2.Type");

		assertTypeRef("DefaultPackageType", "DefaultPackageType");
		assertTypeRef("pckg1.Type", "Type");
		assertTypeRef("pckg2.Type", "pckg2.Type");

		assertImports("pckg1.Type");
	}

	@Test
	public void typeFromSamePackage() throws Exception {
		manager.useType("my.package.Type");

		assertTypeRef("my.package.Type", "Type");
		assertImports();
	}

	@Test
	public void typeFromSameDiffPackage_First() throws Exception {
		manager.useType("other.package.Type");
		manager.useType("my.package.Type");

		assertTypeRef("my.package.Type", "Type");
		assertTypeRef("other.package.Type", "other.package.Type");
		assertImports();
	}

	@Test
	public void typeFromSameDiffPackage_Last() throws Exception {
		manager.useType("my.package.Type");
		manager.useType("other.package.Type");

		assertTypeRef("my.package.Type", "Type");
		assertTypeRef("other.package.Type", "other.package.Type");
		assertImports();
	}

	@Test
	public void javaLangType() throws Exception {
		manager.useType("java.lang.String");

		assertTypeRef("java.lang.String", "String");
		assertImports();
	}

	@Test
	public void javaLangTypeNamesake_DifferentPackage_First() throws Exception {
		manager.useType("pckg.String");
		manager.useType("java.lang.String");

		assertTypeRef("java.lang.String", "String");
		assertTypeRef("pckg.String", "pckg.String");
		assertImports();
	}

	@Test
	public void javaLangTypeNamesake_DifferentPackage_Last() throws Exception {
		manager.useType("java.lang.String");
		manager.useType("pckg.String");

		assertTypeRef("java.lang.String", "String");
		assertTypeRef("pckg.String", "pckg.String");
		assertImports();
	}

	@Test
	public void testImportsGrouped() throws Exception {
		manager.useType("bbb.Bb1");
		manager.useType("aaa.Type1");
		manager.useType("aaa.Type2");

		List<List<String>> groups = manager.getTypesToImportInGroups();
		assertThat(groups).hasSize(2);
		
		assertThat(groups.get(0)).containsExactly("aaa.Type1", "aaa.Type2");
		assertThat(groups.get(1)).containsExactly("bbb.Bb1");
		
	}

	private void assertTypeRef(String fullName, String expectedTypeRef) {
		assertThat(manager.getTypeRef(fullName)).isEqualTo(expectedTypeRef);
	}

	private void assertImports(String... imports) {
		Set<String> actualImports = newSet(manager.getTypesToImport());

		assertThat(actualImports).isEqualTo(asSet(imports));
	}

}
