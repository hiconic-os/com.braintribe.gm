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
package com.braintribe.codec.jseval.genericmodel;

import java.util.HashSet;
import java.util.Set;

public class JseUtil {
	private static final String startMarker = "//BEGIN_TYPES"; 
	private static final String endMarker = "//END_TYPES"; 
	public static Set<String> extractTypes(String jseSource) {
		Set<String> typeNames = new HashSet<String>();
		
		int stIndex = jseSource.indexOf(';');
		
		if (stIndex == -1)
			return typeNames;
		
		stIndex = jseSource.indexOf(';', stIndex + 1);
		
		if (stIndex == -1)
			return typeNames;
		
		int s = jseSource.substring(0, stIndex).indexOf(startMarker);
		
		if (s == -1)
			return typeNames;
		
		int e = jseSource.indexOf(endMarker);
		
		String typeNamesSection = jseSource.substring(s + startMarker.length(), e);
		String typeNamesStatements[] = typeNamesSection.split(";");
		
		for (String typeNameFragment: typeNamesStatements) {
			int startQuoteIndex = typeNameFragment.indexOf('"');
			int endQuoteIndex = typeNameFragment.lastIndexOf('"');
			
			if (startQuoteIndex != -1 && endQuoteIndex != -1) {
				String typeName = typeNameFragment.substring(startQuoteIndex + 1, endQuoteIndex);
				typeNames.add(typeName);
			}
		}
		
		return typeNames;
	}
}
