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
package com.braintribe.model.processing.vde.impl.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.vde.impl.root.VariableVdeTest;


/**
 * An  implementation of {@link Function} that is used for in {@link VariableVdeTest}
 *
 */
public class VariableCustomProvider implements Function<Variable, String> {

	private final Map<String,String> map;
	
	public VariableCustomProvider() {
		map = new HashMap<String, String>();
	}

	public void add(String key, String value){
		map.put(key, value);
	}

	@Override
	public String apply(Variable index) throws RuntimeException {
		return map.get(index.getName());
	}

}
