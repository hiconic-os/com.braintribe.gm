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
import static com.braintribe.model.processing.validation.expert.CommonChecks.areValuesUnique;
import static com.braintribe.model.processing.validation.expert.CommonChecks.isNotNull;
import static com.braintribe.utils.lcd.CollectionTools.isEmpty;
import static com.braintribe.utils.lcd.StringTools.isEmpty;

import java.util.stream.Collectors;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.override.GmPropertyOverride;
import com.braintribe.model.processing.validation.ValidationContext;

public class DeclaredEntityTypeValidationTask implements ValidationTask {

	private GmMetaModel model;
	private GmEntityType type;

	public DeclaredEntityTypeValidationTask(GmMetaModel model, GmEntityType type) {
		this.model = model;
		this.type = type;
	}

	@Override
	public void execute(ValidationContext context) {
		if (isEmpty(type.getGlobalId())) {
			context.addValidationMessage(type, ERROR, "Global id is missing");
		}
		if (!areValuesUnique(type.getProperties().stream().map(GmProperty::getName).collect(Collectors.toList()))) {
			context.addValidationMessage(type, ERROR, "Duplicate property names");
		}
		if (type.getProperties().contains(null)) {
			context.addValidationMessage(type, ERROR, "Null values in property collection");
		}
		type.getProperties().stream() //
				.filter(CommonChecks::isNotNull) //
				.map(this::declaredPropertyValidationTask) //
				.forEach(context::addValidationTask);
		if (isNotNull(type.getEvaluatesTo())) {
			context.addValidationTask(referencedTypeValidationTask(type.getEvaluatesTo()));
		}
		if (type.getMetaData().contains(null)) {
			context.addValidationMessage(type, ERROR, "Null values in meta data collection");
		}
		type.getMetaData().stream() //
				.filter(CommonChecks::isNotNull) //
				.map(CoreMetaDataValidationTask::new) //
				.forEach(context::addValidationTask);
		if (type.getPropertyMetaData().contains(null)) {
			context.addValidationMessage(type, ERROR, "Null values in property meta data collection");
		}
		type.getPropertyMetaData().stream() //
				.filter(CommonChecks::isNotNull) //
				.map(CoreMetaDataValidationTask::new) //
				.forEach(context::addValidationTask);
		if (type.getPropertyOverrides().contains(null)) {
			context.addValidationMessage(type, ERROR, "Null values in property override collection");
		}
		type.getPropertyOverrides().stream() //
				.filter(CommonChecks::isNotNull) //
				.map(this::declaredPropertyOverrideValidationTask) //
				.forEach(context::addValidationTask);
		if (isEmpty(type.getSuperTypes())) {
			context.addValidationMessage(type, ERROR, "No super types found");
		} else {
			if (type.getSuperTypes().contains(null)) {
				context.addValidationMessage(type, ERROR, "Null values in super type collection");
			}
			type.getSuperTypes().stream() //
					.filter(CommonChecks::isNotNull) //
					.map(this::referencedTypeValidationTask) //
					.forEach(context::addValidationTask);
		}
	}

	private ReferencedTypeValidationTask referencedTypeValidationTask(GmType type) {
		return new ReferencedTypeValidationTask(model, type);
	}

	private DeclaredPropertyValidationTask declaredPropertyValidationTask(GmProperty property) {
		return new DeclaredPropertyValidationTask(model, type, property);
	}

	private DeclaredPropertyOverrideValidationTask declaredPropertyOverrideValidationTask(
			GmPropertyOverride propertyOverride) {
		return new DeclaredPropertyOverrideValidationTask(model, type, propertyOverride);
	}
}
