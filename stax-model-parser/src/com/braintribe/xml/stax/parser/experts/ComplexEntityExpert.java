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
package com.braintribe.xml.stax.parser.experts;

import java.util.Map;

import org.xml.sax.Attributes;

import com.braintribe.codec.Codec;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;


public class ComplexEntityExpert extends AbstractComplexEntityExpert {
	
	public ComplexEntityExpert(EntityType<GenericEntity> type, String property, Map<String, Codec<GenericEntity, String>> codecs) {
		this.type = type;
		this.property = property;
		this.codecs = codecs;
	}
	
	public ComplexEntityExpert(String signature, String property, Map<String, Codec<GenericEntity, String>> codecs) {
		this.type = GMF.getTypeReflection().getEntityType(signature);
		this.property = property;
		this.codecs = codecs;
	}
	
	@Override
	public EntityType<GenericEntity> getType() {
		return type;
	}
	public void setType(EntityType<GenericEntity> type) {
		this.type = type;
	}

	@Override
	public void startElement(ContentExpert parent, String uri, String localName, String qName, Attributes atts)  {
		if (property == null)
			property = qName;
	}

	@Override
	public void endElement(ContentExpert parent, String uri, String localName, String qName) {		
		if (parent != null)
			parent.attach( this);
	}


	
	
	
}
