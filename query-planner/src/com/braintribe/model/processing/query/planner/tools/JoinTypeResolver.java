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
package com.braintribe.model.processing.query.planner.tools;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.processing.query.planner.condition.JoinPropertyType;
import com.braintribe.model.processing.query.tools.SourceTypeResolver;
import com.braintribe.model.query.Join;

/**
 * 
 */
public class JoinTypeResolver {

	public static JoinPropertyType resolveJoinPropertyType(Join join) {
		EntityType<?> sourceType = SourceTypeResolver.resolveType(join.getSource());

		return resolveJoinPropertyType(sourceType, join.getProperty());
	}

	public static JoinPropertyType resolveJoinPropertyType(EntityType<?> operandType, String propertyName) {
		GenericModelType propertyType = operandType.getProperty(propertyName).getType();

		switch (propertyType.getTypeCode()) {
			case listType:
				return JoinPropertyType.list;
			case mapType:
				return JoinPropertyType.map;
			case setType:
				return JoinPropertyType.set;
			case entityType:
				return JoinPropertyType.entity;
			default:
				return JoinPropertyType.simple;
		}
	}

}
