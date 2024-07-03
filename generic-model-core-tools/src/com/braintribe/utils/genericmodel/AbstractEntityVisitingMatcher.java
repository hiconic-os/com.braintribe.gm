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

import java.util.Collection;
import java.util.Map;

import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.CriterionType;
import com.braintribe.model.generic.pr.criteria.matching.Matcher;
import com.braintribe.model.generic.reflection.EntityVisitor;
import com.braintribe.model.generic.reflection.TraversingContext;
import com.braintribe.model.generic.reflection.TraversingVisitor;

/**
 * An abstract <code>Matcher</code> that matches, if the current object cannot lead to an entity (--&gt; for example,
 * simple types or enums). The idea is to make sure that all reachable entities will be visited. Whether an entity is
 * matched or not, depends on the {@link #matchesEntity(GenericEntity) implementation} (one might want to visit only
 * specific entities). Note that this method will only be invoked on {@link CriterionType#ENTITY} level. Therefore this
 * <code>Matcher</code> is intended to be used in combination with {@link TraversingVisitor vistors}s that visit
 * entities on that level (only), see e.g. {@link EntityVisitor}.
 *
 * @author michael.lafite
 */
abstract class AbstractEntityVisitingMatcher implements Matcher {

	@Override
	public boolean matches(final TraversingContext traversingContext) {

		final CriterionType criterionType = traversingContext.getCurrentCriterionType();
		final Object value = traversingContext.getObjectStack().peek();

		final boolean result;

		switch (criterionType) {
			case BASIC:// fall through
			case MAP:// fall through
			case MAP_ENTRY:// fall through
				// we don't know yet whether the current path leads to an entity, so we have to return false, i.e.
				// continue
				result = false;
				break;
			case ROOT: // fall through
			case PROPERTY: // fall through
				// if the value is not an entity or a collection/map, we can match (i.e. stop) here
				result = !(value instanceof GenericEntity || value instanceof Collection || value instanceof Map);
				break;
			case SET_ELEMENT: // fall through
			case LIST_ELEMENT: // fall through
			case MAP_KEY: // fall through
			case MAP_VALUE: // fall through
				// only if the value is a GenericEntity, we have to continue (we can stop for simple types)
				result = !(value instanceof GenericEntity);
				break;
			case ENTITY:
				result = matchesEntity((GenericEntity) value);
				break;
			default:
				throw new UnknownEnumException(criterionType);
		}

		return result;
	}

	protected abstract boolean matchesEntity(GenericEntity genericEntity);

}
