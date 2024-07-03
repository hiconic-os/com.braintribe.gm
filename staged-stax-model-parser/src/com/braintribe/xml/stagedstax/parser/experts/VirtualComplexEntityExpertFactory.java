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

import java.util.Map;

import com.braintribe.codec.Codec;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.xml.stagedstax.parser.factory.AbstractContentExpertFactory;
import com.braintribe.xml.stagedstax.parser.factory.ContentExpertFactory;

public class VirtualComplexEntityExpertFactory extends AbstractContentExpertFactory implements ContentExpertFactory {
	protected EntityType<GenericEntity> type;
	protected String property;
	protected String signature;
	protected Map<String, Codec<GenericEntity, String>> codecs;
	
	public VirtualComplexEntityExpertFactory(String property, Map<String, Codec<GenericEntity, String>> codecs) {
		this.type = null;
		this.property = property;
		this.codecs = codecs;
	}
		  
	@Override
	public ContentExpert newInstance() {		
		return new VirtualComplexEntityExpert();		
	}
}
