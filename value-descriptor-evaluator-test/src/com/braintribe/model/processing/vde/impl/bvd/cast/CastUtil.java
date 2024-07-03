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
package com.braintribe.model.processing.vde.impl.bvd.cast;

import java.math.BigDecimal;

public class CastUtil {

	public static Object[] getAllPossibleNumberTypesArray() {
		
		Object[] result = new Object[5];
		result[0] = new Integer(5);
		result[1] = new Long(5);
		result[2] = new Float(5);
		result[3] = new Double(5);
		result[4] = new BigDecimal(5);
		return result;
	}

}
