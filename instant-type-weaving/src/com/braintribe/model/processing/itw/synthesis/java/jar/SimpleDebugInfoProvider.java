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
package com.braintribe.model.processing.itw.synthesis.java.jar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.braintribe.model.processing.itw.asm.DebugInfoProvider;

/**
 * 
 */
public class SimpleDebugInfoProvider implements DebugInfoProvider {

	private final Map<String, EntityDebugInfo> entityDebugInfos;

	public SimpleDebugInfoProvider(Map<String, String> entitySources) {
		entityDebugInfos = new HashMap<String, EntityDebugInfo>();

		for (Entry<String, String> entry: entitySources.entrySet()) {
			String typeSignature = entry.getKey();
			String source = entry.getValue();

			entityDebugInfos.put(typeSignature, new EntityDebugInfo(source));
		}
	}

	@Override
	public boolean hasInfoFor(String typeSignature) {
		return entityDebugInfos.containsKey(typeSignature);
	}

	@Override
	public Integer getMethodLine(String className, String methodName) {
		EntityDebugInfo info = entityDebugInfos.get(className);
		return info != null ? info.getMethodLine(methodName) : null;
	}

	@Override
	public String getSetterParameterName(String className, String setterName) {
		return entityDebugInfos.get(className).getSetterParameterName(setterName);
	}

}
