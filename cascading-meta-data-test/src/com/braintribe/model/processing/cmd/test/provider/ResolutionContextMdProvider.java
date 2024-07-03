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

import com.braintribe.model.processing.cmd.test.meta.entity.SimpleEntityMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumConstantMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumMetaData;
import com.braintribe.model.processing.cmd.test.meta.model.SimpleModelMetaData;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.meta.selector.AspectCheckingSelector;
import com.braintribe.model.processing.cmd.test.model.Color;
import com.braintribe.model.processing.cmd.test.model.Person;

/**
 * 
 */
public class ResolutionContextMdProvider extends AbstractModelSupplier {

	private static final AspectCheckingSelector selector = AspectCheckingSelector.T.create();

	@Override
	protected void addMetaData() {
		// Model MD
		fullMdEditor.addModelMetaData(append(newMd(SimpleModelMetaData.T, true), selector));

		// Entity + Property MD
		fullMdEditor.onEntityType(Person.T) //
				.addMetaData(append(newMd(SimpleEntityMetaData.T, true), selector)) //
				.addPropertyMetaData("name", append(newMd(SimplePropertyMetaData.T, true), selector));

		fullMdEditor.onEnumType(Color.class) //
				.addMetaData(append(newMd(SimpleEnumMetaData.T, true), selector)) //
				.addConstantMetaData(Color.GREEN, append(newMd(SimpleEnumConstantMetaData.T, true), selector));
	}

}
