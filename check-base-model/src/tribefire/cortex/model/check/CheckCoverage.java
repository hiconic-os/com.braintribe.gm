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
package tribefire.cortex.model.check;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

/**
 * Classifies different purposes of checks. This classification makes no statement about {@link CheckWeight performance}.
 * 
 */
public enum CheckCoverage implements EnumBase {
	
	/**
	 * Covers checks in order to check the ability to process functionality
	 * and not the functionality itself. Such checks must not involve connectivity to remote components as the meaning
	 * of these checks is to control the lifecycle of processing instances in a cluster.
	 * <p>
	 * Examples:
	 * <ul>
	 * 	<li>Memory
	 * 	<li>CPU load
	 * 	<li>Pooling
	 * 		<ul>
	 * 			<li>Threads
	 * 			<li>HTTP Connections
	 * 			<li>Database Connections
	 * 		</ul>
	 * </li>
	 * 	<li>Deadlock Detection
	 * </ul>
	 * 
	 */
	vitality,
	
	/**
	 * Covers checks in order to check the ability to connect to remote components like databases, hosts, queues,...
	 * This can evolve minimal functional usage like doing a query to a systable in order to provoke a roundtrip in
	 * case a library does not offer dedicated connectivity testing.
	 */
	connectivity,
	
	/**
	 * Covers checks of any kind of actual functionality.
	 */
	functional;

	public static final EnumType T = EnumTypes.T(CheckCoverage.class);

	@Override
	public EnumType type() {
		return T;
	}
}
