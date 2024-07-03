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
package com.braintribe.model.processing.core.expert.impl;

import com.braintribe.model.generic.reflection.GenericModelType;

public class GmExpertKey {
	
	private Class<?> expertClass;
	private GenericModelType denotationType;
	
	/**
	 * Creates a new <code>ExpertKey</code> instance.
	 * 
	 * @param expertClass
	 *            the expert (super) type. If there e.g. exists an interface <code>PersonExpert</code> and a concrete
	 *            implementation <code>EmployeeExpert</code> one would pass the <code>PersonExpert</code> class.
	 * @param denotationType
	 *            the class
	 */
	public GmExpertKey(Class<?> expertClass, GenericModelType denotationType) {
		this.expertClass = expertClass;
		this.denotationType = denotationType;
	}
	
	public Class<?> getExpertClass() {
		return expertClass;
	}
	
	public GenericModelType getDenotationType() {
		return denotationType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expertClass == null) ? 0 : expertClass.hashCode());
		result = prime * result
				+ ((denotationType == null) ? 0 : denotationType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GmExpertKey other = (GmExpertKey) obj;
		if (expertClass == null) {
			if (other.expertClass != null)
				return false;
		} else if (expertClass != other.expertClass)
			return false;
		if (denotationType == null) {
			if (other.denotationType != null)
				return false;
		} else if (denotationType != other.denotationType)
			return false;
		return true;
	}
	
}
