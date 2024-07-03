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
package com.braintribe.xml.stax.parser.registry;

import java.util.HashMap;
import java.util.Map;

import com.braintribe.xml.stax.parser.experts.ContentExpert;

public class AbstractContentExpertRegistry implements ContentExpertRegistry {
	private Map<String, ContentExpertFactory> tagToExpertMap = new HashMap<String, ContentExpertFactory>();
	private Map<String, ContentExpertFactory> globalTagToExpertMap = new HashMap<String, ContentExpertFactory>();
	
	@Override
	public void addExpertFactory(String tag, ContentExpertFactory factory) {
		if (tag.startsWith("*")) {
			String suspect = tag.substring(tag.indexOf('/') + 1);			
			globalTagToExpertMap.put( suspect, factory);
		}
		else {
			tagToExpertMap.put( tag, factory);
		}
	}

	@Override
	public synchronized ContentExpert getExpertForTag(String tag) {		
		ContentExpertFactory factory = null;
		
		int indexOf = tag.lastIndexOf('/');
		// no last, so it must be the main container
		if (indexOf < 0) {
			factory = tagToExpertMap.get(tag);
			if (factory == null)
				return null;
		}		
		else {				
			// might be a global processing instruction
			String pi = tag.substring( indexOf + 1);
			factory = globalTagToExpertMap.get( pi);
			// no global, try standard expert 
			if (factory == null) {
				factory = tagToExpertMap.get(tag);
			}		
			// no standard, might be a repeating expert for children 
			if (factory == null) {
				String parent = tag.substring(0, indexOf);		
				factory = tagToExpertMap.get( parent + "/*");
				if (factory == null)
					return null;
			}
		}
		
		return factory.newInstance();
	}
	
	
	
}
