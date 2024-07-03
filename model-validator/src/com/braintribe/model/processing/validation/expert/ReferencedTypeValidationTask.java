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
import static com.braintribe.model.processing.validation.ValidationMode.DECLARATION;

import java.util.Optional;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.validation.ValidationContext;

public class ReferencedTypeValidationTask implements ValidationTask {

	private GmMetaModel model;
	private GmType type;

	public ReferencedTypeValidationTask(GmMetaModel model, GmType type) {
		this.model = model;
		this.type = type;
	}

	@Override
	public void execute(ValidationContext context) {
		if (model.equals(type.getDeclaringModel())) {
			Optional<GmType> typeOptional = model.getTypes().stream() //
					.filter(CommonChecks::isNotNull) //
					.filter(t -> t.equals(type)) //
					.findFirst();
			if (!typeOptional.isPresent()) {
				context.addValidationMessage(type, ERROR, "Not found in declaring model type collection");
			}
		} else {
			Optional<GmMetaModel> depOptional = model.getDependencies().stream() //
					.filter(CommonChecks::isNotNull) //
					.filter(d -> d.equals(type.getDeclaringModel())) //
					.findFirst();
			if (!depOptional.isPresent()) {
				context.addValidationMessage(type, ERROR, "Declaring model not found in model dependency collection");
			}
		}
		if (context.getMode().equals(DECLARATION)) {
			context.addValidationTask(new TypeValidationTask(type));
		}
	}
}
