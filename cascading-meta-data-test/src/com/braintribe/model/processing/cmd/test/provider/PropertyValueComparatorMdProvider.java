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

import com.braintribe.model.meta.selector.Operator;
import com.braintribe.model.meta.selector.PropertyValueComparator;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.model.Person;

/**
 * 
 */
public class PropertyValueComparatorMdProvider extends AbstractModelSupplier {

	@Override
	protected void addMetaData() {
		addPropertyMdWithStringEqualComparator();
		addPropertyMdWithIntGreaterComparator();
		addPropertyMdWithCollectionFirstElementEqualComparator();
		addPropertyMdWithListSizeComparator();
		addPropertyMdWithSetSizeComparator();
		addPropertyMdWithMapSizeComparator();
	}

	private void addPropertyMdWithStringEqualComparator() {
		PropertyValueComparator comparator = PropertyValueComparator.T.create();
		comparator.setOperator(Operator.equal);
		comparator.setValue("foo");

		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", append(newMd(SimplePropertyMetaData.T, true), comparator));
	}

	private void addPropertyMdWithIntGreaterComparator() {
		PropertyValueComparator comparator = PropertyValueComparator.T.create();
		comparator.setPropertyPath("age");
		comparator.setOperator(Operator.greater);
		comparator.setValue(0);

		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", append(newMd(SimplePropertyMetaData.T, true), comparator));
	}

	private void addPropertyMdWithCollectionFirstElementEqualComparator() {
		PropertyValueComparator comparator = PropertyValueComparator.T.create();
		comparator.setPropertyPath("friends.0.name");
		comparator.setOperator(Operator.equal);
		comparator.setValue("foo");

		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", append(newMd(SimplePropertyMetaData.T, true), and(comparator, useCase("firstElement"))));
	}

	private void addPropertyMdWithListSizeComparator() {
		PropertyValueComparator comparator = PropertyValueComparator.T.create();
		comparator.setPropertyPath("friends.size");
		comparator.setOperator(Operator.equal);
		comparator.setValue(2);

		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", append(newMd(SimplePropertyMetaData.T, true), and(comparator, useCase("size-list"))));
	}

	private void addPropertyMdWithSetSizeComparator() {
		PropertyValueComparator comparator = PropertyValueComparator.T.create();
		comparator.setPropertyPath("otherFriends.size");
		comparator.setOperator(Operator.equal);
		comparator.setValue(2);

		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", append(newMd(SimplePropertyMetaData.T, true), and(comparator, useCase("size-set"))));
	}

	private void addPropertyMdWithMapSizeComparator() {
		PropertyValueComparator comparator = PropertyValueComparator.T.create();
		comparator.setPropertyPath("properties.size");
		comparator.setOperator(Operator.greater);
		comparator.setValue(1);

		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", append(newMd(SimplePropertyMetaData.T, true), and(comparator, useCase("size-map"))));
	}

}
