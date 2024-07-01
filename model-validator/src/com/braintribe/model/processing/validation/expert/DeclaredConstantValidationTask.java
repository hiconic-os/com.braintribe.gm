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
