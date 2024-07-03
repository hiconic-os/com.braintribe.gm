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
package com.braintribe.model.processing.misc.tools;

import com.braintribe.model.generic.path.api.IModelPathElement;

/**
 * {@link IModelPathElement} console printer
 *
 */
public class IModelPathElementPrinter {

	public static void print(IModelPathElement element) {
		IModelPathElementPrinter.printHelper(element);
	}

	private static void printHelper(IModelPathElement element) {

		IModelPathElement currentElement = element;
		while (currentElement != null) {

			printElement(currentElement);
			currentElement = currentElement.getPrevious();

		}

	}

	private static void printElement(IModelPathElement element) {

		// TODO adjust formatting

		println("Element" + "(" + number(element) + ") [");

		println("\t Element Type: " + element.getElementType().name());

		println("\t Type: " + element.getType());

		println("\t Value: " + element.getValue());

		println("\t Depth: " + element.getDepth());

		println("]");
	}

	private static int number(IModelPathElement element) {
		return System.identityHashCode(element);
	}

	private static void println(Object s) {
		print(s + "\n");
	}

	private static void print(Object s) {
		System.out.print(s);
	}

}
