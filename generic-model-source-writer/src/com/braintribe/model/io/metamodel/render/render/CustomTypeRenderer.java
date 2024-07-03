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
package com.braintribe.model.io.metamodel.render.render;

import java.util.Collection;
import java.util.List;

import com.braintribe.model.generic.tools.AbstractStringifier;

/**
 * @author peter.gazdik
 */
public abstract class CustomTypeRenderer extends AbstractStringifier {

	protected void printPackage(String packageName) {
		if (packageName.isEmpty())
			return;

		println("package ", packageName, ";");
		println();
	}

	protected void printImportGroups(List<List<String>> importGrouss) {
		for (List<String> imports : importGrouss)
			printImports(imports);
	}

	protected void printImports(Collection<String> imports) {
		if (imports.isEmpty())
			return;

		for (String i : imports)
			println("import ", i, ";");

		println();
	}

	protected void printAnnotations(List<String> annotations) {
		for (String annotation : annotations)
			println("@", annotation);
	}

	protected void printTypeEnd() {
		print("}");
	}
}
