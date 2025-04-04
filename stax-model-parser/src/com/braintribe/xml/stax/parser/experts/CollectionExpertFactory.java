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

import com.braintribe.codec.Codec;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.xml.stax.parser.registry.ContentExpertFactory;

public class CollectionExpertFactory<T> extends ComplexEntityExpertFactory implements ContentExpertFactory{
	private String collection;

	/*
	public CollectionExpertFactory(EntityType<GenericEntity> type, String collection, String property) {
		super( type, property);
		this.collection = collection;
	}
   */
	public CollectionExpertFactory(String signature, String collection, String property, Map<String, Codec<GenericEntity, String>> codecs) {
		super( signature, property, codecs);
		this.collection = collection;
	}
	public CollectionExpertFactory(String signature, String collection, String property) {
		super( signature, property, null);
		this.collection = collection;
	}

	@Override
	public ContentExpert newInstance() {
		if (type != null) {
			return new CollectionExpert<T>(type, collection, property, codecs);
		}
		else {
			return new CollectionExpert<T>(signature, collection, property, codecs);
		}
	}
	
	
	

}
