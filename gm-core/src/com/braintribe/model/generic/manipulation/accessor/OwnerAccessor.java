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
package com.braintribe.model.generic.manipulation.accessor;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.value.EntityReference;

@SuppressWarnings("deprecation")
public abstract class OwnerAccessor<T extends Owner> implements ExpertRegistryAware {
	
	protected GenericModelTypeReflection typeReflection = GMF.getTypeReflection();
	protected ExpertRegistry expertRegistry;
	
	@Override
	public void setExpertRegistry(ExpertRegistry expertRegistry) {
		this.expertRegistry = expertRegistry;
	}
	
	/** @deprecated use {@link GMF#getTypeReflection()} */
	@Deprecated
	public GenericModelTypeReflection getGenericModelTypeReflection() {
		return typeReflection;
	}
	
	public abstract <T1> T1 get(T owner);
	
	/**
	 * @return if the replace change the identification a new reference is returned otherwise null
	 */
	public abstract EntityReference replace(T owner, Object newValue);
	public abstract void markAsAbsent(T owner, AbsenceInformation absenceInformation, String propertyName);

}
