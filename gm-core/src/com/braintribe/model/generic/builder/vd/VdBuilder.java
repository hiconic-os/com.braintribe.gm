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
package com.braintribe.model.generic.builder.vd;

import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.GlobalEntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.generic.value.PreliminaryEntityReference;

/**
 * @author peter.gazdik
 */
public class VdBuilder {

	public static PersistentEntityReference persistentReference(String typeSignature, Object id, String partition) {
		return reference(PersistentEntityReference.T, typeSignature, id, partition);
	}

	public static PreliminaryEntityReference preliminaryReference(String typeSignature, Object id, String partition) {
		return reference(PreliminaryEntityReference.T, typeSignature, id, partition);
	}

	public static GlobalEntityReference globalReference(String typeSignature, Object id) {
		return reference(GlobalEntityReference.T, typeSignature, id, null);
	}

	public static <T extends EntityReference> T referenceWithNewPartition(T ref, String newPartition) {
		return VdBuilder.reference(ref.entityType(), ref.getTypeSignature(), ref.getRefId(), newPartition);
	}

	public static <T extends EntityReference> T reference(EntityType<T> entityType, String typeSignature, Object id, String partition) {
		T result = entityType.create();
		result.setTypeSignature(typeSignature);
		result.setRefId(id);
		result.setRefPartition(partition);
		return result;
	}

	public static AbsenceInformation absenceInformation(int size) {
		AbsenceInformation result = AbsenceInformation.T.create();
		result.setSize(size);
		return result;
	}

}
