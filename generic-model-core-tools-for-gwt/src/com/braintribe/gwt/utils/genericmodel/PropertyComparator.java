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
package com.braintribe.gwt.utils.genericmodel;

import java.util.Comparator;

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.utils.lcd.CommonTools;

/**
 * A {@link Property} comparator that {@link #compare(Property, Property) compares} properties by their
 * {@link Property#getName()}.
 *
 * @author michael.lafite
 */
public class PropertyComparator implements Comparator<Property> {

	@Override
	public int compare(final Property prop1, final Property prop2) {
		if (prop1 == null || prop2 == null) {
			throw new NullPointerException(
					"Can't compare properties because at least one is not set! " + CommonTools.getParametersString("prop1", prop1, "prop2", prop2));
		}
		return prop1.getName().compareTo(prop2.getName());
	}

}
