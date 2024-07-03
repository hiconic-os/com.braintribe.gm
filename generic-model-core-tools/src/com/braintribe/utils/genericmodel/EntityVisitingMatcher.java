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
package com.braintribe.utils.genericmodel;

import java.util.Set;

import com.braintribe.common.lcd.Empty;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.utils.lcd.NullSafe;

/**
 * {@link AbstractEntityVisitingMatcher} implementation that doesn't {@link #matchesEntity(GenericEntity) match}
 * entities, except for the {@link #setEntitiesToMatch(Set) entities to match}.
 *
 * @author michael.lafite
 */
public class EntityVisitingMatcher extends AbstractEntityVisitingMatcher {

	private Set<? extends GenericEntity> entitiesToMatch = Empty.set();

	public EntityVisitingMatcher() {
		// nothing to do
	}

	public EntityVisitingMatcher(final Set<? extends GenericEntity> entitiesToMatch) {
		if (entitiesToMatch == null) {
			this.entitiesToMatch = Empty.set();
		} else {
			this.entitiesToMatch = entitiesToMatch;
		}
	}

	public void setEntitiesToMatch(final Set<? extends GenericEntity> entitiesToMatch) {
		this.entitiesToMatch = entitiesToMatch;
	}

	/**
	 * Doesn't match, i.e. returns <code>false</code>, unless the <code>entity</code> is one of the
	 * {@link #entitiesToMatch entities to match}.
	 */
	@Override
	protected boolean matchesEntity(final GenericEntity entity) {
		if (NullSafe.contains(this.entitiesToMatch, entity)) {
			return true;
		}
		return false;
	}

}
