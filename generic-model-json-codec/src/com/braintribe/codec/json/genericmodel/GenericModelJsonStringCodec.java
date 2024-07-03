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
package com.braintribe.codec.json.genericmodel;

import com.braintribe.cfg.Configurable;
import com.braintribe.codec.json.AbstractJsonToStringCodec;
import com.braintribe.codec.marshaller.api.GmCodec;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.fasterxml.jackson.databind.JsonNode;

public class GenericModelJsonStringCodec<T> extends AbstractJsonToStringCodec<T> {
	private GenericModelJsonCodec<T> jsonCodec = new GenericModelJsonCodec<T>();

	@Configurable
	public void setAssignAbsenceInformationForMissingProperties(boolean assignAbsenceInformatonForMissingProperties) {
		jsonCodec.setAssignAbsenceInformationForMissingProperties(assignAbsenceInformatonForMissingProperties);
	}

	@Configurable
	public void setGenericModelTypeReflection(
			GenericModelTypeReflection genericModelTypeReflection) {
		jsonCodec.setGenericModelTypeReflection(genericModelTypeReflection);
	}

	@Configurable
	public void setCreateEnhancedEntities(boolean createEnhancedEntities) {
		jsonCodec.setCreateEnhancedEntities(createEnhancedEntities);
	}

	@Configurable
	public void setWriteAbsenceInformation(boolean writeAbsenceInformation) {
		jsonCodec.setWriteAbsenceInformation(writeAbsenceInformation);
	}

	@Configurable
	public void setType(GenericModelType type) {
		jsonCodec.setType(type);
	}
	
	@Override
	public GmCodec<T, JsonNode> getJsonDelegateCodec() {
		return jsonCodec;
	}
}
