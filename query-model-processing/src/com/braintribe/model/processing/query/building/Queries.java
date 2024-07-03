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
package com.braintribe.model.processing.query.building;

import java.util.Set;

import com.braintribe.model.query.Operator;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.conditions.Conjunction;
import com.braintribe.model.query.conditions.Disjunction;
import com.braintribe.model.query.conditions.Negation;
import com.braintribe.model.query.conditions.ValueComparison;
import com.braintribe.model.query.functions.EntitySignature;
import com.braintribe.model.query.functions.aggregate.Count;

public abstract class Queries {
	public static Conjunction and(Condition... conditions) {
		return Conjunction.of(conditions);
	}

	public static Disjunction or(Condition... conditions) {
		return Disjunction.of(conditions);
	}

	public static Negation not(Condition condition) {
		return Negation.of(condition);
	}

	public static ValueComparison compare(Object op1, Operator operator, Object op2) {
		return ValueComparison.compare(op1, operator, op2);
	}

	public static ValueComparison eq(Object op1, Object op2) {
		return ValueComparison.eq(op1, op2);
	}

	public static ValueComparison ne(Object op1, Object op2) {
		return ValueComparison.ne(op1, op2);
	}

	public static ValueComparison gt(Object op1, Object op2) {
		return ValueComparison.gt(op1, op2);
	}

	public static ValueComparison ge(Object op1, Object op2) {
		return ValueComparison.ge(op1, op2);
	}

	public static ValueComparison lt(Object op1, Object op2) {
		return ValueComparison.lt(op1, op2);
	}

	public static ValueComparison le(Object op1, Object op2) {
		return ValueComparison.le(op1, op2);
	}

	public static ValueComparison in(Object element, Set<?> set) {
		return ValueComparison.in(element, set);
	}

	public static ValueComparison in(Object element, Object collection) {
		return compare(element, Operator.in, collection);
	}

	public static ValueComparison like(Object value, String pattern) {
		return ValueComparison.like(value, pattern);
	}

	public static ValueComparison ilike(Object value, String pattern) {
		return ValueComparison.ilike(value, pattern);
	}

	public static EntitySignature entitySignature(Object operand) {
		return EntitySignature.of(operand);
	}

	public static Count count(Object operand) {
		return Count.of(operand);
	}

	public static Count count(Object operand, boolean distinct) {
		Count count = Count.of(operand);
		count.setDistinct(distinct);
		return count;
	}

}