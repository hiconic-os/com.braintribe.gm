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

import com.braintribe.model.generic.builder.meta.MetaModelBuilder;
import com.braintribe.model.meta.selector.AccessTypeSelector;
import com.braintribe.model.meta.selector.AccessTypeSignatureSelector;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.model.Access;
import com.braintribe.model.processing.cmd.test.model.IncrementalAccess;
import com.braintribe.model.processing.cmd.test.model.Person;

/**
 * 
 */
public class SpecialSelectorMdProvider extends AbstractModelSupplier {

	public static final Access accessSelector = Access.T.create();
	public static final AccessTypeSelector accessTypeSelector = AccessTypeSelector.T.create();
	public static final AccessTypeSignatureSelector accessTypeSignatureSelector = AccessTypeSignatureSelector.T.create();

	public static final String NO_UC = "NO_UC";
	public static final String JUST_UC = "UCE-1";
	public static final String NOT_UC = "NOT-UC";
	public static final String X_AND_UC = "X_AND_UC";
	public static final String X_OR_UC = "X_OR_UC";

	static {
		accessSelector.setExternalId("theAccess");

		// matches every access
		accessTypeSelector.setAccessType(MetaModelBuilder.entityType(IncrementalAccess.T.getTypeSignature()));
		accessTypeSelector.setAssignable(true);
		accessTypeSignatureSelector.setDenotationTypeSignature(IncrementalAccess.T.getTypeSignature());
		accessTypeSignatureSelector.setAssignable(true);
	}

	@Override
	protected void addMetaData() {
		addSimplePropertyMdWithAccessSelector();
		addSimplePropertyMdWithAccessTypeSelector();
		addSimplePropertyMdWithAccessTypeSignatureSelector();
		addSimplePropertyMdForIgnoreSelectors();
	}

	private void addSimplePropertyMdWithAccessSelector() {
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", append(newMd(SimplePropertyMetaData.T, true), accessSelector));
	}

	private void addSimplePropertyMdWithAccessTypeSelector() {
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("age", append(newMd(SimplePropertyMetaData.T, true), accessTypeSelector));
	}

	private void addSimplePropertyMdWithAccessTypeSignatureSelector() {
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("friend", append(newMd(SimplePropertyMetaData.T, true), accessTypeSignatureSelector));
	}

	private void addSimplePropertyMdForIgnoreSelectors() {
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("friends", //
						append(newMd(SimplePropertyMetaData.T, JUST_UC), useCase(JUST_UC), 4), //
						append(newMd(SimplePropertyMetaData.T, X_AND_UC), and(FALSE_SELECTOR, useCase(X_AND_UC)), 3), //
						append(newMd(SimplePropertyMetaData.T, X_OR_UC), or(FALSE_SELECTOR, useCase(X_OR_UC)), 2), //
						append(newMd(SimplePropertyMetaData.T, NO_UC), staticContextSelector("NO_UC_VALUE"), 1) //
				);
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("otherFriends", //
						append(newMd(SimplePropertyMetaData.T, NOT_UC), not(useCase(NOT_UC))));

	}

}
