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
import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.StringTools.isEmpty;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.mapping.Alias;
import com.braintribe.model.meta.data.mapping.PositionalArguments;
import com.braintribe.model.processing.validation.ValidationContext;

public class CoreMetaDataValidationTask implements ValidationTask {

	private MetaData metaData;
	private final List<EntityType<?>> allowedMetadata = asList(Alias.T, PositionalArguments.T); // TODO create essential meta data list

	public CoreMetaDataValidationTask(MetaData metaData) {
		this.metaData = metaData;
	}

	@Override
	public void execute(ValidationContext context) {
		if (isEmpty(metaData.getGlobalId())) {
			context.addValidationMessage(metaData, ERROR, "Global id is missing");
		}
		if (!allowedMetadata.contains(metaData.type())) {
			context.addValidationMessage(metaData, ERROR, "Only essential meta data is allowed");
		}
		if (isNotNull(metaData.getSelector())) {
			context.addValidationMessage(metaData, ERROR, "Meta data selector is not allowed");
		}
	}
}
