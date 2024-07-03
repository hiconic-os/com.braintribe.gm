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
package com.braintribe.xml.stagedstax.parser.experts;

import org.xml.sax.Attributes;

public class VirtualComplexEntityExpert extends AbstractComplexEntityExpert {

	@Override
	public void startElement(ContentExpert parent, String uri, String localName, String qName, Attributes atts)  {
		this.instance = parent.getInstance();
		this.type = parent.getType();
		if (type == null) {
			type = instance.entityType();
		}
		
		if (property == null)
			property = qName;
	}

	@Override
	public void endElement(ContentExpert parent, String uri, String localName, String qName)  {
	}

	
}
