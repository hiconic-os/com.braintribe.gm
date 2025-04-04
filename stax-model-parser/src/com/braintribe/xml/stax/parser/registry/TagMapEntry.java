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

public class TagMapEntry implements ContentExpertFactory {
	private String tag;
	private Map<String, TagMapEntry> children = new HashMap<String, TagMapEntry>();
	private ContentExpertFactory expertFactory;
	
	
	public TagMapEntry(String tag, ContentExpertFactory expertFactory) {
		this.tag = tag;
		this.expertFactory = expertFactory;
	}
	
	public String getTag() {
		return tag;
	}
	
	public TagMapEntry getEntry( String child) {
		return children.get(child);
	}
	
	public void addChild( TagMapEntry entry) {
		children.put( entry.getTag(), entry);
	}
	
	@Override
	public ContentExpert newInstance() {
		return expertFactory.newInstance();
	}
	
	
}
