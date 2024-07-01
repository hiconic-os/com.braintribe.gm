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
package com.braintribe.model.processing.meta.editor;

import java.util.Arrays;
import java.util.HashSet;

import com.braintribe.model.meta.selector.AccessSelector;
import com.braintribe.model.meta.selector.ConjunctionSelector;
import com.braintribe.model.meta.selector.DisjunctionSelector;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.meta.selector.NegationSelector;
import com.braintribe.model.meta.selector.RoleSelector;
import com.braintribe.model.meta.selector.UseCaseSelector;

public interface Selectors {

	// ###################################################
	// ## . . . . . . . . . Logical . . . . . . . . . . ##
	// ###################################################

	public static DisjunctionSelector disjunction() {
		return DisjunctionSelector.T.create();
	}

	public static DisjunctionSelector disjunction(MetaDataSelector... operands) {
		DisjunctionSelector selector = disjunction();
		selector.setOperands(Arrays.asList(operands));
		return selector;
	}

	public static ConjunctionSelector conjunction() {
		return ConjunctionSelector.T.create();
	}

	public static ConjunctionSelector conjunction(MetaDataSelector... operands) {
		ConjunctionSelector selector = conjunction();
		selector.setOperands(Arrays.asList(operands));
		return selector;
	}

	public static NegationSelector negation() {
		return NegationSelector.T.create();
	}

	public static NegationSelector negation(MetaDataSelector operand) {
		NegationSelector selector = negation();
		selector.setOperand(operand);
		return selector;
	}

	// ###################################################
	// ## . . . . . . . . . . Misc . . . . . . . . . . .##
	// ###################################################

	public static UseCaseSelector useCase(String useCase) {
		UseCaseSelector selector = UseCaseSelector.T.create();
		selector.setUseCase(useCase);
		return selector;
	}

	public static AccessSelector access(String externalId) {
		AccessSelector selector = AccessSelector.T.create();
		selector.setExternalId(externalId);
		return selector;
	}

	public static RoleSelector roles(String... roles) {
		RoleSelector selector = RoleSelector.T.create();
		selector.setRoles(new HashSet<>(Arrays.asList(roles)));
		return selector;
	}

	// ###################################################
	// ## . . . . . . Property Discriminators . . . . . ##
	// ###################################################

}
