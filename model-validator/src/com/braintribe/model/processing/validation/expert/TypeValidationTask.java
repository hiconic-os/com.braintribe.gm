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
import static com.braintribe.model.processing.validation.expert.CommonChecks.isNotNull;
import static com.braintribe.utils.lcd.StringTools.isEmpty;

import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.validation.ValidationContext;

public class TypeValidationTask implements ValidationTask {

	private GmType type;

	public TypeValidationTask(GmType type) {
		this.type = type;
	}

	@Override
	public void execute(ValidationContext context) {
		if (isEmpty(type.getGlobalId())) {
			context.addValidationMessage(type, ERROR, "Global id is missing");
		}
		if (!isNotNull(type.getDeclaringModel())) {
			context.addValidationMessage(type, ERROR, "Declaring model is missing");
		}
		if (isEmpty(type.getTypeSignature())) {
			context.addValidationMessage(type, ERROR, "Type signature is missing");
		} else {
			try {
				EntityTypes.get(type.getTypeSignature());
			} catch (GenericModelException e) {
				context.addValidationMessage(type, ERROR,
						"Type signature points to unexisting type");
			}
		}
	}
}
