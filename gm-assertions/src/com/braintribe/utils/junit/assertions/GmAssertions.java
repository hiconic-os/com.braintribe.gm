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
package com.braintribe.utils.junit.assertions;

import com.braintribe.model.generic.GenericEntity;

/**
 * A {@link BtAssertions} that provides an entry point for assertions methods for instances of {@link GenericEntity}
 * type.
 * 
 * 
 */
public class GmAssertions extends BtAssertions {

	/**
	 * Creates a new instance of {@link GenericEntityAssert}.
	 * 
	 * @param actual
	 *            the value to be the target of the assertions methods.
	 * @return the created assertion object.
	 */
	public static GenericEntityAssert assertThat(final GenericEntity actual) {
		return new GenericEntityAssert(actual);
	}

	/**
	 * Creates a new instance of {@link GenericEntityPropertyAssert}.
	 * 
	 * @param actual
	 *            the value to be the target of the assertions methods.
	 * @return the created assertion object.
	 */
	public static GenericEntityPropertyAssert assertThat(final GenericEntityProperty actual) {
		return new GenericEntityPropertyAssert(actual);
	}

	/**
	 * Convenience method to create a {@link GenericEntityProperty}.
	 */
	public static GenericEntityProperty property(final GenericEntity entity, final String name) {
		return new GenericEntityProperty(entity, name);
	}
}
