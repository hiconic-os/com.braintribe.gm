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
package com.braintribe.model.access.security.testdata;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.constraint.Mandatory;
import com.braintribe.model.meta.data.constraint.NonDeletable;
import com.braintribe.model.meta.data.constraint.NonInstantiable;
import com.braintribe.model.meta.data.constraint.Unique;
import com.braintribe.model.meta.data.constraint.Unmodifiable;
import com.braintribe.model.meta.data.prompt.Confidential;
import com.braintribe.model.meta.data.prompt.Hidden;
import com.braintribe.model.meta.data.query.NonQueryable;
import com.braintribe.model.meta.data.security.Administrable;
import com.braintribe.model.meta.selector.ConjunctionSelector;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.meta.selector.RoleSelector;
import com.braintribe.model.meta.selector.UseCaseSelector;

/**
 * @author peter.gazdik
 */
public class MdFactory {

	public static Mandatory mandatory() {
		return Mandatory.T.create();
	}

	public static Unique unique() {
		return Unique.T.create();
	}

	public static NonInstantiable instantiationDisabled() {
		return NonInstantiable.T.create();
	}

	public static NonDeletable entityDeletionDisabled() {
		return NonDeletable.T.create();
	}

	public static Unmodifiable nonModifiable() {
		return Unmodifiable.T.create();
	}

	public static NonQueryable nonQueryable() {
		return NonQueryable.T.create();
	}

	public static Hidden invisible() {
		return Hidden.T.create();
	}

	public static Confidential confidential() {
		return Confidential.T.create();
	}

	public static MetaData administrable(String role, UseCaseSelector useCaseSelector) {
		Administrable result = Administrable.T.create();
		result.setSelector(conjunctionSelector(useCaseSelector, roleSelector(role)));

		return result;
	}

	// #######################################################
	// ## . . . . . . . . . . Selectors . . . . . . . . . . ##
	// #######################################################

	public static ConjunctionSelector conjunctionSelector(MetaDataSelector... selectors) {
		ConjunctionSelector result = ConjunctionSelector.T.create();
		result.setOperands(asList(selectors));

		return result;
	}

	public static RoleSelector roleSelector(String role) {
		RoleSelector result = RoleSelector.T.create();
		result.setRoles(asSet(role));

		return result;
	}

	public static UseCaseSelector useCaseSelector(String useCase) {
		UseCaseSelector result = UseCaseSelector.T.create();
		result.setUseCase(useCase);

		return result;
	}

}
