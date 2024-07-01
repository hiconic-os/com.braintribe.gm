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
package com.braintribe.model.processing;

import com.braintribe.model.meta.GmIntegerType;

/**
 * Every single test must use this as it's super-type.
 * <p>
 * There is an issues that only happens when the ITW is accessed for the very first time. In order to ensure this issue is covered, we do it in the
 * static initializer here, and as long as every test class extends this class, this will be executed as the very first access to ITW.
 * 
 * @author peter.gazdik
 */
public class ImportantItwTestSuperType {

	static {
		/* This code checks that the very first access to ITW will use protoAnalysis, because regular analysis would fail, as that would need this
		 * type to already exist.
		 * 
		 * There was a time where we only used proto analysis, but that way we couldn't support  */
		GmIntegerType.T.create();
	}

}
