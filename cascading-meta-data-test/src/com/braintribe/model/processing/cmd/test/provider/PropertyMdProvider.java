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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.typecondition.basic.IsAssignableTo;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.meta.selector.PropertyNameSelector;
import com.braintribe.model.meta.selector.PropertyRegexSelector;
import com.braintribe.model.meta.selector.PropertyTypeSelector;
import com.braintribe.model.processing.cmd.test.meta.property.GlobalPropertyMetaData;
import com.braintribe.model.processing.cmd.test.meta.property.GlobalSelectorPMetaData;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.model.Person;
import com.braintribe.model.processing.cmd.test.model.Teacher;

/**
 * 
 */
public class PropertyMdProvider extends AbstractModelSupplier {

	@Override
	protected void addMetaData() {
		addSimplePropertyMd();
		addMdForExtendedInfo();
		addGlobalPropertyMd();
		addGlobalPropertyMdByName();
		addGlobalPropertyMdByRegex();
		addGlobalPropertyMdByType();
	}

	private void addSimplePropertyMd() {
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("age", newMd(SimplePropertyMetaData.T, true));
	}

	private void addMdForExtendedInfo() {
		fullMdEditor.onEntityType(Teacher.T) //
				.addPropertyMetaData("name", newMd(SimplePropertyMetaData.T, true));

		baseMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", newMd(SimplePropertyMetaData.T, true));
	}

	private void addGlobalPropertyMd() {
		fullMdEditor.onEntityType(GenericEntity.T) //
				.addPropertyMetaData(globalMd("GLOBAL"));

		baseMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", globalMd("PROPERTY"));
	}

	private void addGlobalPropertyMdByName() {
		PropertyNameSelector selector = PropertyNameSelector.T.create();
		selector.setPropertyName("color");

		fullMdEditor.onEntityType(GenericEntity.T) //
				.addPropertyMetaData(globalMd("P_NAME", selector));
	}

	private void addGlobalPropertyMdByRegex() {
		PropertyRegexSelector selector = PropertyRegexSelector.T.create();
		selector.setRegex(".*nds");

		fullMdEditor.onEntityType(GenericEntity.T) //
				.addPropertyMetaData(globalMd("P_REGEX", selector));
	}

	private void addGlobalPropertyMdByType() {
		IsAssignableTo condition = IsAssignableTo.T.create();
		condition.setTypeSignature(Person.T.getTypeSignature());

		PropertyTypeSelector selector = PropertyTypeSelector.T.create();
		selector.setTypeCondition(condition);

		fullMdEditor.onEntityType(GenericEntity.T) //
				.addPropertyMetaData(globalMd("P_TYPE", selector));
	}

	private GlobalSelectorPMetaData globalMd(String activeString, MetaDataSelector selector) {
		return append(newMd(GlobalSelectorPMetaData.T, activeString), selector);
	}

	private GlobalPropertyMetaData globalMd(String activeString) {
		return newMd(GlobalPropertyMetaData.T, activeString);
	}

}
