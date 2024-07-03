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

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;
import com.braintribe.model.io.metamodel.render.context.ConstantDescriptor;
import com.braintribe.model.io.metamodel.render.context.EnumTypeContext;
import com.braintribe.model.io.metamodel.render.context.ImportManager;

/**
 * @author peter.gazdik
 */
public class EnumRenderer extends CustomTypeRenderer {

	private final EnumTypeContext context;
	private final ImportManager im;

	public EnumRenderer(EnumTypeContext context) {
		this.context = context;
		this.im = context.importManager;
	}

	public String render() {
		printPackage(context.typeInfo.packageName);
		printImportGroups(context.importManager.getTypesToImportInGroups());
		printAnnotations(context.annotations);

		printTypeHeader();
		printTypeBody();
		printTypeEnd();

		return builder.toString();
	}

	private void printTypeHeader() {
		println("public enum ", context.typeInfo.simpleName, " implements ", im.getTypeRef(EnumBase.class), " {");
	}

	private void printTypeBody() {
		levelUp();

		printConstants();
		printTypeLiteral();
		printTypeMethod();

		levelDown();
	}

	private void printConstants() {
		for (ConstantDescriptor cd : context.constants)
			printConstant(cd);

		println(";");
	}

	private void printConstant(ConstantDescriptor cd) {
		println();
		for (String annotation : cd.annotations)
			println("@", annotation);

		println(cd.name, ",");
	}

	private void printTypeLiteral() {
		print("public static final ", im.getTypeRef(EnumType.class), " T = ", im.getTypeRef(EnumTypes.class), ".T(");
		print(context.typeInfo.simpleName);
		println(".class);");
		println();
	}

	private void printTypeMethod() {
		println("@Override");
		println("public " + im.getTypeRef(EnumType.class) + " type() {");

		levelUp();
		println("return T;");
		levelDown();

		println("}");
		println();
	}

}
