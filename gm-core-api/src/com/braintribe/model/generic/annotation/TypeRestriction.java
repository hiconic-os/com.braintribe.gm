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
package com.braintribe.model.generic.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import com.braintribe.model.generic.annotation.meta.TypeSpecification;

/**
 * Specifies a hard restriction on a type of given property and is primarily used when we want the allow two or more
 * different types for a property. In that case we use a common super-type as the type of the property alongside with
 * this annotation.
 * 
 * In case of a collection we only specify the types of elements or key/value for maps. It is therefore not possible to
 * restrict a type of an Object property to a collection with this annotation.
 * 
 * For example meta-data of a GmProperty can be either UniversalMetaData or PropertyMetaData.
 * 
 * @see TypeSpecification
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface TypeRestriction {

	Class<?>[] value() default {};

	/** This can only be non-empty if the annotated property is of type {@link Map} */
	Class<?>[] key() default {};

	boolean allowVd() default false;

	boolean allowKeyVd() default false;

	String globalId() default "";
}
