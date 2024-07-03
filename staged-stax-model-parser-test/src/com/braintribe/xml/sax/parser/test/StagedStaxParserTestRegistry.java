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
package com.braintribe.xml.sax.parser.test;

import com.braintribe.xml.sax.parser.test.model.Entry;
import com.braintribe.xml.stagedstax.parser.experts.ComplexEntityExpertFactory;
import com.braintribe.xml.stagedstax.parser.experts.EnumValueExpertFactory;
import com.braintribe.xml.stagedstax.parser.experts.ManipulationParserExpertFactory;
import com.braintribe.xml.stagedstax.parser.experts.StringValueExpertFactory;
import com.braintribe.xml.stagedstax.parser.experts.VirtualCollectionExpertFactory;
import com.braintribe.xml.stagedstax.parser.registry.AbstractContentExpertRegistry;

public class StagedStaxParserTestRegistry extends AbstractContentExpertRegistry {

	
	public StagedStaxParserTestRegistry() {
		addExpertFactory( "*/?auto", new StringValueExpertFactory("autoValue"));
		 
		
		addExpertFactory( "container", new ComplexEntityExpertFactory("com.braintribe.xml.sax.parser.test.model.Container", null));
		addExpertFactory( "container/string", new StringValueExpertFactory("stringValue"));
		addExpertFactory( "container/boolean", new StringValueExpertFactory("booleanValue"));
		addExpertFactory( "container/int", new StringValueExpertFactory("integerValue"));
		addExpertFactory( "container/setEntries", new VirtualCollectionExpertFactory<String>("stringSet", null));
		addExpertFactory( "container/setEntries/entry", new StringValueExpertFactory());
		addExpertFactory( "container/listEntries", new VirtualCollectionExpertFactory<String>("stringList", null));
		addExpertFactory( "container/listEntries/entry", new StringValueExpertFactory());
		addExpertFactory( "container/?instruction", new StringValueExpertFactory("processingInstruction"));
		addExpertFactory( "container/?grouping", new EnumValueExpertFactory("com.braintribe.xml.sax.parser.test.model.Grouping"));
		
		addExpertFactory( "container/complexEntries", new VirtualCollectionExpertFactory<Entry>("entries", null));
		addExpertFactory( "container/complexEntries/entry", new ComplexEntityExpertFactory( "com.braintribe.xml.sax.parser.test.model.Entry", null, null));
		addExpertFactory( "container/complexEntries/entry/value",  new StringValueExpertFactory());
		addExpertFactory( "container/complexEntries/entry/?enrich",  new ManipulationParserExpertFactory());
		
	}
}
