// ============================================================================
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
package com.braintribe.gwt.utils.genericmodel;

import java.util.Comparator;

import com.braintribe.model.generic.GenericEntity;

/**
 * {@link Comparator} that compares enties based on their {@link GMCoreTools#getSimpleDescription(GenericEntity) simple
 * description}. For convenience other types are supported as well. For these types just the string representation is
 * used.
 *
 * @author michael.lafite
 */
public class SimpleDescriptionBasedComparator implements Comparator<Object> {

	@Override
	public int compare(final Object object1, final Object object2) {
		final String description1 = getSimpleDescription(object1);
		final String description2 = getSimpleDescription(object2);
		return description1.compareTo(description2);
	}

	public String getSimpleDescription(final Object object) {
		if (object instanceof GenericEntity) {
			return GMCoreTools.getSimpleDescription((GenericEntity) object);
		}
		return String.valueOf(object);
	}
}
