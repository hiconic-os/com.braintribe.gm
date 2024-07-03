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
package com.braintribe.xml.stagedstax.parser.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.braintribe.xml.stagedstax.parser.experts.ContentExpert;
import com.braintribe.xml.stagedstax.parser.factory.ContentExpertFactory;

public class AbstractContentExpertRegistry implements ContentExpertRegistry {
	private Map<String, ContentExpertFactory> globalTagToExpertMap = new HashMap<String, ContentExpertFactory>();
	private ContentExpertFactory rootFactory;
	private Stack<ContentExpertFactory> factoryStack = new Stack<>();
	
	@Override
	public void addExpertFactory(String tag, ContentExpertFactory factory) {
		if (tag.startsWith("*")) {
			String suspect = tag.substring(tag.indexOf('/') + 1);			
			globalTagToExpertMap.put( suspect, factory);
		}
		else {
			String [] tags = tag.split( "/");
			int len  = tags.length;
			if (len == 1) {
				rootFactory = factory;
			}
			else {
				ContentExpertFactory handlingFactory = rootFactory;
				ContentExpertFactory parentFactory = rootFactory;
				for (int i = 1; i < len-1; i++) {
					handlingFactory = parentFactory.getChainedFactory(tags[i]);
					parentFactory = handlingFactory;
				}	
				handlingFactory.chainFactory( tags[len-1], factory);
			}
		}
	}

	@Override
	public synchronized ContentExpert pushTag(String tag) {		
		ContentExpertFactory factory = null;
		
		ContentExpertFactory currentFactory = factoryStack.isEmpty() ? null : factoryStack.peek();
	
		if (currentFactory == null) {
			factoryStack.push( rootFactory);
		}		
		else {				
			// might be a global processing instruction
			if (tag.startsWith( "?")) {
			
				factory = globalTagToExpertMap.get( tag);
				if (factory != null) {
					factoryStack.push(factory);
					return factory.newInstance();
				}	
			}
			// no global, try standard expert 
			factory = currentFactory.getChainedFactory( tag);
			if (factory != null) {
				// no standard, might be a repeating expert for children 
				factoryStack.push(factory);
			}
			else {
				factory = currentFactory.getChainedFactory("*");
				if (factory != null) {
					factoryStack.push(factory);
				}
				else {
					//System.out.println("no factory for tag [" + tag + "]");
					return null;
				}
			}
		}
	
		return factoryStack.peek().newInstance();
	}

	@Override
	public void popTag(String tag) {
		factoryStack.pop();
		
	}
	
	
	
	
}
