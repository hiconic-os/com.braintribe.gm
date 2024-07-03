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
import com.braintribe.xml.stagedstax.parser.factory.ContentExpertFactory;


public class VirtualCollectionExpertFactory<T> extends VirtualComplexEntityExpertFactory implements ContentExpertFactory{
	private String collection;

	public VirtualCollectionExpertFactory(String collection, String property, Map<String, Codec<GenericEntity, String>> codecs) {
		super(property, codecs);
		this.collection = collection;
	}
	public VirtualCollectionExpertFactory(String collection, String property) {
		super(property, null);
		this.collection = collection;
	}

	
	@Override
	public ContentExpert newInstance() {		
		return new VirtualCollectionExpert<T>(collection, property, codecs);		
	}
			
}
