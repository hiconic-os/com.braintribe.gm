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

import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated Please use the GmExpertRegistry of GmCoreCommons
 */
@Deprecated
public class ExpertRegistry {
	private ExpertRegistry parent;
	
	private Map<ExpertKey, Object> expertMap;
	
	protected void initializeExperts() {
		if (expertMap != null) {
			for (Object expert: expertMap.values()) {
				initializeExpert(expert);
			}
		}
	}
	
	protected void initializeExpert(Object expert) {
		if (expert instanceof ExpertRegistryAware) {
			((ExpertRegistryAware) expert).setExpertRegistry(this);
		}
	}
	
	public void setParent(ExpertRegistry parent) {
		this.parent = parent;
	}

	public void setExpertMap(Map<ExpertKey, Object> expertMap) {
		this.expertMap = expertMap;
		initializeExperts();
	}

	public Map<ExpertKey, Object> getExpertMap() {
		return expertMap;
	}
	
	public <T> T getExpert(Class<T> expertClass, Class<?> handledClass) {
		return getExpert(new ExpertKey(expertClass, handledClass));
	}
	
	public <T> T getExpert(Class<T> expertClass, Object handledObject) {
		return getExpert(expertClass, handledObject != null? handledObject.getClass(): Void.class);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getExpert(ExpertKey expertKey) {
		T expert = (T)getDirectExpert(expertKey);
		
		if (expert != null || expertKey.getHandledClass().getSuperclass() == null) {
			return expert;
		} else {
			return (T) getExpert(prepareKeyWithSuperClass(expertKey));
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getDirectExpert(ExpertKey expertKey) {
		T expert = (T) expertMap.get(expertKey);
		
		if (expert == null && parent != null)
			expert = parent.getDirectExpert(expertKey);
		
		return expert;
	}
	
	private ExpertKey prepareKeyWithSuperClass(ExpertKey expertKey) {
		return new ExpertKey(expertKey.getExpertClass(), expertKey.getHandledClass().getSuperclass());
	}
	
	public <T, T1 extends T> void registerExpert(Class<T> expertClass,
			Class<?> handledClass, T1 expert) {
		initializeExpert(expert);

		Map<ExpertKey, Object> expertMap = getExpertMap();
		if (expertMap == null) {
			expertMap = new HashMap<ExpertKey, Object>();
			setExpertMap(expertMap);
		}

		expertMap.put(new ExpertKey(expertClass, handledClass), expert);
	}
}
