// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.validation.expert;

import static com.braintribe.model.processing.validation.ValidationMessageLevel.ERROR;
import static com.braintribe.model.processing.validation.expert.CommonChecks.isNotNull;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.override.GmPropertyOverride;
import com.braintribe.model.processing.validation.ValidationContext;

public class DeclaredPropertyOverrideValidationTask implements ValidationTask {

	private GmMetaModel model;
	private GmEntityType declaringType;
	private GmPropertyOverride propertyOverride;

	public DeclaredPropertyOverrideValidationTask(GmMetaModel model, GmEntityType declaringType,
			GmPropertyOverride propertyOverride) {
		this.model = model;
		this.declaringType = declaringType;
		this.propertyOverride = propertyOverride;
	}

	@Override
	public void execute(ValidationContext context) {
		if (!declaringType.equals(propertyOverride.getDeclaringTypeInfo())) {
			context.addValidationMessage(propertyOverride, ERROR, "Declaring type info is wrong");
		}
		if (!isNotNull(propertyOverride.getProperty())) {
			context.addValidationMessage(propertyOverride, ERROR, "Property is missing");
		} else {
			context.addValidationTask(new ReferencedPropertyValidationTask(model, propertyOverride.getProperty()));
			if (isNotNull(propertyOverride.getInitializer())) {
				context.addValidationTask(new InitializerValidationTask(propertyOverride.getProperty()));
			}
		}
		if (propertyOverride.getMetaData().contains(null)) {
			context.addValidationMessage(propertyOverride, ERROR, "Null values in meta data collection");
		}
		propertyOverride.getMetaData().stream() //
				.filter(CommonChecks::isNotNull) //
				.map(CoreMetaDataValidationTask::new) //
				.forEach(context::addValidationTask);
	}
}
