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
package com.braintribe.model.processing.smood.tools;

import java.util.Map;

import com.braintribe.cc.lcd.CodingMap;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.commons.EntRefHashingComparator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.generic.value.EntityReference;

/**
 * 
 */
public class DuplicatesRemover {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	public static <T> T removeDuplicates(T t) {
		GenericModelType type = typeReflection.getType(t);

		T clone = (T) type.clone(new DuplicateRemovingContext(), t, StrategyOnCriterionMatch.reference);

		return clone;
	}

	private static class DuplicateRemovingContext extends StandardCloningContext {
		private final Map<EntityReference, GenericEntity> entityByReference = CodingMap.create(EntRefHashingComparator.INSTANCE);

		@Override
		public GenericEntity preProcessInstanceToBeCloned(GenericEntity instanceToBeCloned) {
			EntityReference ref = instanceToBeCloned.reference();
			GenericEntity other = entityByReference.get(ref);
			
			if (other != null) {
				return other;
			}
			
			entityByReference.put(ref, instanceToBeCloned);
			return instanceToBeCloned;
		}

		@Override
		public GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType, GenericEntity instanceToBeCloned) {
			return instanceToBeCloned;
		}

	}
}
