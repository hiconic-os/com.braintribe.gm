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
package com.braintribe.model.processing.cmd.test.provider;

import com.braintribe.model.meta.data.constraint.Mandatory;
import com.braintribe.model.meta.data.constraint.Unmodifiable;
import com.braintribe.model.meta.data.prompt.Confidential;
import com.braintribe.model.meta.data.prompt.Hidden;
import com.braintribe.model.meta.data.prompt.NonConfidential;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.cmd.test.model.Person;

/**
 * 
 */
public class PredicateMdProvider extends AbstractModelSupplier {

	public static final String PREDICATE = "predicate";
	public static final String PREDICATE_ERASURE = "predicateErasure";
	public static final String EXPLICIT_PREDICATE = "explicitPredicate";
	public static final String EXPLICIT_PREDICATE_ERASURE = "explicitPredicateErasure";

	public static final String UNMODIFIABLE_PROPETY = "longValue";
	public static final String UNMODIFIABLE_MANDTORY_PROPETY = "name";

	@Override
	protected void addMetaData() {
		addPredicateMd();
		addPredicateErasureMd();
		addExplicitPredicateMd();
		addExplicitPredicateErasureMd();
		addUnmodifiableProperty();
		addUnmodifiableMandatoryProperty();
	}

	private void addPredicateMd() {
		fullMdEditor. //
				addModelMetaData(append(Visible.T.create(), useCase(PREDICATE)));
	}

	private void addPredicateErasureMd() {
		fullMdEditor. //
				addModelMetaData(append(Hidden.T.create(), useCase(PREDICATE_ERASURE)));
	}

	private void addExplicitPredicateMd() {
		fullMdEditor. //
				addModelMetaData(append(Confidential.T.create(), useCase(EXPLICIT_PREDICATE)));
	}

	private void addExplicitPredicateErasureMd() {
		fullMdEditor. //
				addModelMetaData(append(NonConfidential.T.create(), useCase(EXPLICIT_PREDICATE_ERASURE)));
	}

	private void addUnmodifiableProperty() {
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData(UNMODIFIABLE_PROPETY, Unmodifiable.T.create());
	}

	private void addUnmodifiableMandatoryProperty() {
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData(UNMODIFIABLE_MANDTORY_PROPETY, Unmodifiable.T.create(), Mandatory.T.create());
	}

}
