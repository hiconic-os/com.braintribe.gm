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
package com.braintribe.model.processing.meta.oracle.flat;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.Map;

import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.meta.override.GmEnumConstantOverride;
import com.braintribe.model.meta.override.GmEnumTypeOverride;

/**
 * Factory for flat local constants.
 * 
 * @author peter.gazdik
 */
public class FlatConstantsFactory {

	private final FlatEnumType flatEnumType;
	private final Map<String, FlatEnumConstant> flatConstants;

	public static Map<String, FlatEnumConstant> buildFor(FlatEnumType flatEnumType) {
		return new FlatConstantsFactory(flatEnumType).build();
	}

	FlatConstantsFactory(FlatEnumType flatEnumType) {
		this.flatEnumType = flatEnumType;
		this.flatConstants = newMap();
	}

	private Map<String, FlatEnumConstant> build() {
		for (GmEnumTypeInfo entityTypeInfo : flatEnumType.infos)
			visit(entityTypeInfo);

		return flatConstants;
	}

	private void visit(GmEnumTypeInfo gmEnumTypeInfo) {
		if (gmEnumTypeInfo instanceof GmEnumTypeOverride) {
			GmEnumTypeOverride gmEnumTypeOverride = (GmEnumTypeOverride) gmEnumTypeInfo;
			for (GmEnumConstantOverride gmEnumConstantOverride : nullSafe(gmEnumTypeOverride.getConstantOverrides()))
				visitEnumConstant(gmEnumConstantOverride);

		} else {
			GmEnumType gmEnumType = (GmEnumType) gmEnumTypeInfo;
			for (GmEnumConstantInfo gmEnumConstantInfo : nullSafe(gmEnumType.getConstants()))
				visitEnumConstant(gmEnumConstantInfo);
		}
	}

	private void visitEnumConstant(GmEnumConstantInfo gmEnumConstantInfo) {
		FlatEnumConstant flatConstant = acquireFlatConstant(gmEnumConstantInfo);
		flatConstant.infos.add(gmEnumConstantInfo);
	}

	private FlatEnumConstant acquireFlatConstant(GmEnumConstantInfo info) {
		GmEnumConstant gmEnumConstant = info.relatedConstant();
		String constantName = gmEnumConstant.getName();

		return flatConstants.computeIfAbsent(constantName, n -> new FlatEnumConstant(gmEnumConstant));
	}

}
