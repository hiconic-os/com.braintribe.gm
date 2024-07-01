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
package com.braintribe.model.processing.itw.asm;

/**
 * 
 */
public interface DebugInfoProvider {

	/**
	 * @return <tt>true</tt> iff given provider has some debug-information on given class
	 */
	boolean hasInfoFor(String className);

	/**
	 * Returns the first line of the actual implementation of the method, i.e. for standard getter 3-line getter/setter, it is the line in
	 * the middle (First line is the method header, second is the actual implementation, third is just the "}" sign.)
	 */
	Integer getMethodLine(String className, String methodName);

	/**
	 * For this header like <code>public void setName(String nameParam)</code> the returned value would be "nameParam".
	 */
	String getSetterParameterName(String className, String setterName);

}
