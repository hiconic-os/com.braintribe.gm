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
package com.braintribe.model.processing.query.normalization;

import com.braintribe.model.query.Query;
import com.braintribe.model.query.Restriction;
import com.braintribe.model.query.conditions.AbstractJunction;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.conditions.Negation;
import com.braintribe.utils.lcd.CommonTools;

/**
 * Helper class used to normalize queries. Currently the normalizer ...<br/>
 * <ul>
 * <li>replaces junctions having only a single operand with that operand.</li>
 * <li>removes junctions with no operand.</li>
 * <li>replaces negations where the operand is also a negation with the operand of that child negation.</li>
 * <li>removes negations with no operand.</li>
 * </ul>
 * All normalizations are combined: for example, if the condition to normalize is a negation of a conjunction with a
 * single negation operand that negates a disjunction with a single property condition, the the whole condition will be
 * replaced with that property condition.
 * <p/>
 * Further normalization could be done, e.g. conditions with wildcards that match everything, duplicate conditions, etc.
 * <br/>
 * 
 * @author michael.lafite
 */
public class QueryNormalizer {

	/**
	 * Normalizes the passed query and (for convenience) returns it.
	 * 
	 * @param query
	 *            the query to normalize.
	 * @return the normalized query (not a new instance).
	 */
	public static <T extends Query> T normalizeQuery(final T query) {
		if (query.getRestriction() != null) {
			query.setRestriction(normalizeRestriction(query.getRestriction()));
		}

		return query;
	}

	public static <T extends Restriction> T normalizeRestriction(final T restriction) {

		T result = restriction;

		if (restriction.getCondition() != null) {
			restriction.setCondition(normalizeCondition(restriction.getCondition()));
		}

		if (CommonTools.isAllNull(restriction.getCondition(), restriction.getPaging())) {
			result = null;
		}

		return result;
	}

	public static Condition normalizeCondition(final Condition condition) {
		Condition result = condition;
		if (condition instanceof AbstractJunction) {
			result = normalizeJunction((AbstractJunction) condition);
		} else if (condition instanceof Negation) {
			result = normalizeNegation((Negation) condition);
		}
		return result;
	}

	public static Condition normalizeJunction(final AbstractJunction junction) {

		for (int i = 0; i < junction.getOperands().size(); i++) {
			final Condition condition = junction.getOperands().get(i);

			if (condition instanceof AbstractJunction) {
				final Condition normalizedJunction = normalizeJunction((AbstractJunction) condition);
				if (condition != normalizedJunction) {
					if (normalizedJunction == null) {
						junction.getOperands().remove(i);
					} else {
						junction.getOperands().set(i, normalizedJunction);
					}
				}
			}
		}

		Condition result = junction;
		if (junction.getOperands().size() == 0) {
			// just return null, since we no longer need this condition
			result = null;
		} else if (junction.getOperands().size() == 1) {
			// we no longer need the junction --> return the single property
			result = junction.getOperands().get(0);
		} else {
			// return the junction --> it cannot be further optimized
			result = junction;
		}
		return result;
	}

	public static Condition normalizeNegation(final Negation negation) {

		negation.setOperand(normalizeCondition(negation.getOperand()));

		Condition result = negation;
		if (negation.getOperand() == null) {
			// no reason to negate empty condition
			result = null;
		} else if (negation.getOperand() instanceof Negation) {
			// two negations --> first remove both ...
			result = ((Negation) negation.getOperand()).getOperand();
			// ... and then normalize operand of child negation.
			result = normalizeCondition(result);
		}
		return result;
	}
}
