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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

public class EnumValueExpert extends AbstractContentExpert {
	private String enumClass;
	
	public EnumValueExpert( String enumClass, String property) {
		this.enumClass = enumClass;
		this.property = property;
	}

	@Override
	public void startElement(ContentExpert parent, String uri, String localName, String qName, Attributes atts) {
		if (property == null) {
			property = qName;
		}
	}

	@Override
	public void endElement(ContentExpert parent, String uri, String localName, String qName)  {
		parent.attach(this);
	}

	@Override
	public void attach(ContentExpert child) {	
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getPayload() {
		String value = buffer.toString().trim();
		try {
			return Enum.valueOf((Class<? extends Enum>)Class.forName(enumClass), value);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("enum [" + enumClass + "] cannot be found");
		}
	}

	@Override
	public GenericEntity getInstance() {
		return null;
	}

	@Override
	public EntityType<GenericEntity> getType() {
		return null;
	}
	

}
