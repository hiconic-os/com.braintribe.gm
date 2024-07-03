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
package com.braintribe.model.processing.validation.expert;

import static com.braintribe.model.processing.validation.ValidationMessageLevel.ERROR;
import static com.braintribe.utils.lcd.StringTools.isEmpty;

import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.processing.validation.ValidationContext;

public class DeclaredConstantValidationTask implements ValidationTask {

	private GmEnumType declaringType;
	private GmEnumConstant constant;

	public DeclaredConstantValidationTask(GmEnumType declaringType, GmEnumConstant constant) {
		this.declaringType = declaringType;
		this.constant = constant;
	}

	@Override
	public void execute(ValidationContext context) {
		if (isEmpty(constant.getGlobalId())) {
			context.addValidationMessage(constant, ERROR, "Global id is missing");
		}
		if (!declaringType.equals(constant.getDeclaringType())) {
			context.addValidationMessage(constant, ERROR, "Declaring type is wrong");
		}
		if (isEmpty(constant.getName())) {
			context.addValidationMessage(constant, ERROR, "Name is missing");
		}
		if (constant.getMetaData().contains(null)) {
			context.addValidationMessage(constant, ERROR, "Null values in meta data collection");
		}
		constant.getMetaData().stream() //
				.filter(CommonChecks::isNotNull) //
				.map(CoreMetaDataValidationTask::new) //
				.forEach(context::addValidationTask);
	}
}
