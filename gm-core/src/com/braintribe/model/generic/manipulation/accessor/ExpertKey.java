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

public class ExpertKey {
	
	private Class<?> expertClass;
	private Class<?> handledClass;
	
	/**
	 * Creates a new <code>ExpertKey</code> instance.
	 * 
	 * @param expertClass
	 *            the expert (super) type. If there e.g. exists an interface <code>PersonExpert</code> and a concrete
	 *            implementation <code>EmployeeExpert</code> one would pass the <code>PersonExpert</code> class.
	 * @param handledClass
	 *            the class handled by the
	 */
	public ExpertKey(Class<?> expertClass, Class<?> handledClass) {
		this.expertClass = expertClass;
		this.handledClass = handledClass;
	}
	
	public Class<?> getExpertClass() {
		return expertClass;
	}
	
	public Class<?> getHandledClass() {
		return handledClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expertClass == null) ? 0 : expertClass.hashCode());
		result = prime * result
				+ ((handledClass == null) ? 0 : handledClass.hashCode());
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
		ExpertKey other = (ExpertKey) obj;
		if (expertClass == null) {
			if (other.expertClass != null)
				return false;
		} else if (expertClass != other.expertClass)
			return false;
		if (handledClass == null) {
			if (other.handledClass != null)
				return false;
		} else if (handledClass != other.handledClass)
			return false;
		return true;
	}
	
}
