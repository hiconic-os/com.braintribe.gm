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

import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.validation.ValidationContext;

public class DeclaredTypeValidationTask implements ValidationTask {

	private GmMetaModel declaringModel;
	private GmType type;

	public DeclaredTypeValidationTask(GmMetaModel declaringModel, GmType type) {
		this.declaringModel = declaringModel;
		this.type = type;
	}

	@Override
	public void execute(ValidationContext context) {
		if (!declaringModel.equals(type.getDeclaringModel())) {
			context.addValidationMessage(type, ERROR, "Declaring model is wrong");
		}
		if (type instanceof GmEntityType) {
			context.addValidationTask(new DeclaredEntityTypeValidationTask(declaringModel, (GmEntityType) type));
		} else if (type instanceof GmEnumType) {
			context.addValidationTask(new DeclaredEnumTypeValidationTask((GmEnumType) type));
		} else if (!(type instanceof GmCustomType)) {
			context.addValidationMessage(type, ERROR, "Not a custom type");
		}
		context.addValidationTask(new TypeValidationTask(type));
	}
}
