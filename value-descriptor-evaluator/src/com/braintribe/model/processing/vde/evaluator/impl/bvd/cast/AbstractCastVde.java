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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.cast;

import java.math.BigDecimal;

public class AbstractCastVde {

	protected static boolean isValidCastOperand(Object operand) {
		boolean result = isInteger(operand) || isDouble(operand) || isFloat(operand) || isLong(operand) || isBigDecimal(operand);
		return result;
	}

	private static boolean isInteger(Object operand) {
		return operand instanceof Integer;
	}

	private static boolean isDouble(Object operand) {
		return operand instanceof Double;
	}

	private static boolean isFloat(Object operand) {
		return operand instanceof Float;
	}

	private static boolean isLong(Object operand) {
		return operand instanceof Long;
	}

	private static boolean isBigDecimal(Object operand) {
		return operand instanceof BigDecimal;
	}
}
