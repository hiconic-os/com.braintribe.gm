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
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SimpleType;

/**
 * If <tt>false</tt>, empty properties are not written out.
 * <p>
 * An <i>empty property</i> is a property with a value that corresponds to the default value when a raw instance is created, e.g. via
 * {@link EntityType#createRaw()}. This means:
 * <ul>
 * <li><tt>null</tt> for nullable non-collection properties
 * <li>empty collection for collection properties
 * <li>{@link SimpleType#getDefaultValue() default primitive value} for primitive type properties (such as <tt>false</tt> for boolean).
 * </ul>
 * <p>
 * Note the implementation should in this case do the following check: {@code !.isBase() && property.isEmptyValue(value)}
 * 
 * @see EntityType#isBase()
 * @see Property#isEmptyValue(Object)
 * 
 */
public interface WriteEmptyPropertiesOption extends MarshallerOption<Boolean> {
	// Intentionally left blank - Used as type safe key only
}