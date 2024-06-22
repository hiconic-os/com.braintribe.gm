// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
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
