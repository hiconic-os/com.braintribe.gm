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
package com.braintribe.model.processing.traversing.engine.impl.misc.printer;

import com.braintribe.model.generic.path.api.IPropertyRelatedModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;

public class TraversingModelPathElementPrinter {
	
	@SuppressWarnings("incomplete-switch")
	public static String format(TraversingModelPathElement pathElement) {
		switch (pathElement.getElementType()) {
			case EntryPoint:
				return "Entry point";
			case ListItem:
				return "List item";
			case MapKey:
				return "Map key";
			case MapValue:
				return "Map value";
			case Property:
				IPropertyRelatedModelPathElement propertyElement = (IPropertyRelatedModelPathElement) pathElement;
				return "Property " + propertyElement.getEntityType().getShortName() + "."
						+ propertyElement.getProperty().getName() + " : "
						+ format(propertyElement.getValue() + "");
			case Root:
				return "Root";
			case SetItem:
				return "Set item";
		}
		return "";
	}

	private static String format(String rawString) {
		if (rawString.startsWith("[") && rawString.endsWith("]")) {
			return "List Reference";
		} else if (rawString.contains("@")) {
			return "Reference";
		} else if (rawString.contains("CET") || rawString.contains("CEST")) {
			return "Date";
		} else {
			return rawString;
		}
	}
}
