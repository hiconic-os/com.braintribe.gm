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
package com.braintribe.model.generic.annotation.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.braintribe.model.generic.annotation.TypeRestriction;

/**
 * Specifies what the type of given property should be, but this limitation is not part of the woven type - it is only
 * present as meta-data and it's up to an implementer of given use-case how he uses this.
 * 
 * Typical example is the specification for the id property, where this information is usually respected unless the
 * underlying persistence layer has a good reason not to. For example our CollaborativeSmoodAccess uses only Strings as
 * ids.
 * 
 * @see TypeRestriction
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface TypeSpecification {

	Class<?> value();

	String globalId() default "";
}
