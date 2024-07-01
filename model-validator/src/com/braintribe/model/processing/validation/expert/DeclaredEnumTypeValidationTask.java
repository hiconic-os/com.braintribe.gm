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
import static com.braintribe.model.processing.validation.expert.CommonChecks.areValuesUnique;
import static com.braintribe.utils.lcd.StringTools.isEmpty;

import java.util.stream.Collectors;

import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.processing.validation.ValidationContext;

public class DeclaredEnumTypeValidationTask implements ValidationTask {

	private GmEnumType type;

	public DeclaredEnumTypeValidationTask(GmEnumType type) {
		this.type = type;
	}

	@Override
	public void execute(ValidationContext context) {
		if (isEmpty(type.getGlobalId())) {
			context.addValidationMessage(type, ERROR, "Global id is missing");
		}
		if (!areValuesUnique(type.getConstants().stream().map(GmEnumConstant::getName).collect(Collectors.toList()))) {
			context.addValidationMessage(type, ERROR, "Duplicate constant names");
		}
		if (type.getConstants().contains(null)) {
			context.addValidationMessage(type, ERROR, "Null values in constant collection");
		}
		type.getConstants().stream() //
				.filter(CommonChecks::isNotNull) //
				.map(this::declaredConstantValidationTask) //
				.forEach(context::addValidationTask);
		if (type.getMetaData().contains(null)) {
			context.addValidationMessage(type, ERROR, "Null values in meta data collection");
		}
		type.getMetaData().stream() //
				.filter(CommonChecks::isNotNull) //
				.map(CoreMetaDataValidationTask::new) //
				.forEach(context::addValidationTask);
		if (type.getEnumConstantMetaData().contains(null)) {
			context.addValidationMessage(type, ERROR, "Null values in enum constant meta data collection");
		}
		type.getEnumConstantMetaData().stream() //
				.filter(CommonChecks::isNotNull) //
				.map(CoreMetaDataValidationTask::new) //
				.forEach(context::addValidationTask);
	}

	private DeclaredConstantValidationTask declaredConstantValidationTask(GmEnumConstant enumConstant) {
		return new DeclaredConstantValidationTask(type, enumConstant);
	}
}
