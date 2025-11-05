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
package com.braintribe.codec.marshaller.api.options.attributes;

import com.braintribe.codec.marshaller.api.MarshallerOption;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;

/**
 * If <tt>false</tt>, empty properties are not written out.
 * <p>
 * An <i>empty property</i> is either null or an empty collection, as long as the type of the property is also a collection. Empty collections
 * properties of base type (i.e. Object in Java) will be written.
 * 
 * @see EntityType#isBase()
 * @see GenericModelType#isEmpty(Object)
 * 
 */
public interface WriteEmptyPropertiesOption extends MarshallerOption<Boolean> {
	// Intentionally left blank
}