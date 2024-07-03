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

import com.braintribe.model.processing.cmd.test.meta.enumeration.GlobalEnumConstantMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.ModelEnumConstantMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.ModelEnumMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumConstantMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumConstantOverrideMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumOverrideMetaData;
import com.braintribe.model.processing.cmd.test.model.Color;

/**
 * 
 */
public class EnumMdProvider extends AbstractModelSupplier {

	public static final int PRIORITIZED_MD = 10;

	@Override
	protected void addMetaData() {
		addSimpleEnumTypeMetaData();
		addSimpleEnumOverrideMetaData();
		addSimpleEnumConstantMetaData();
		addSimpleEnumConstantOverrideMetaData();
		addGlobalConstantMd();
		addModelEnumMd();
		addModelConstantMd();
	}

	private void addSimpleEnumTypeMetaData() {
		baseMdEditor.onEnumType(Color.class) //
				.addMetaData(newMd(SimpleEnumMetaData.T, true));
	}

	private void addSimpleEnumOverrideMetaData() {
		fullMdEditor.onEnumType(Color.class) //
				.addMetaData(newMd(SimpleEnumOverrideMetaData.T, true));
	}

	private void addSimpleEnumConstantMetaData() {
		baseMdEditor.onEnumType(Color.class) //
				.addConstantMetaData(Color.GREEN, newMd(SimpleEnumConstantMetaData.T, true));
	}

	private void addSimpleEnumConstantOverrideMetaData() {
		fullMdEditor.onEnumType(Color.class) //
				.addConstantMetaData(Color.GREEN, newMd(SimpleEnumConstantOverrideMetaData.T, true));
	}

	private void addGlobalConstantMd() {
		baseMdEditor.onEnumType(Color.class) //
				.addConstantMetaData(globalMd(false)).addConstantMetaData(Color.GREEN, globalMd(true));
	}

	private GlobalEnumConstantMetaData globalMd(boolean isFromConstant) {
		GlobalEnumConstantMetaData result = newMd(GlobalEnumConstantMetaData.T, true);
		result.setIsFromConstant(isFromConstant);

		return result;
	}

	private void addModelEnumMd() {
		fullMdEditor.addEnumMetaData(newMd(ModelEnumMetaData.T, true));
	}

	private void addModelConstantMd() {
		fullMdEditor.addConstantMetaData(newMd(ModelEnumConstantMetaData.T, true));
	}

}
