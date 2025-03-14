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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.braintribe.model.generic.reflection.ScalarType;

/**
 * Specifies the default value of a property.
 * 
 * Since this is a String value, special care has to be taken that the provided string can be transformed correctly to the target type. Hence, a
 * special encoding has to be used, based on the type of the property. For regular values (not value descriptors) we use the encoding compatible with
 * {@link ScalarType#instanceFromGmString(String)}.
 *
 * <ul>
 * <li>Strings are enclosed in single ticks (e.g., 'Hello, world!')</li>
 * <li>Numbers other than integer need a suffix, indicating their type: 'l' for long, 'f' for float, 'd' for double, and 'b' for BigDecimal</li>
 * <li>Date can be specified as GMT:${longNumber}. For example GMT:0 means 1.1.1970 0:0:0 GMT</li>
 * <li>now() signifies the current Date, i.e. the very moment when the value is being assigned</li>
 * <li>"null" (as a String, without the quotes) means that null should be the initial value</li>
 * <li>Enums can be specified just by the constant name (e.g. green) as long as the enum type can be deduced from the property type (property type is
 * enum, or say list of enums). Otherwise use enum(${fully qualified enum class-name},${constant name}) value (e.g. enum(my.Color,green))</li>
 * <li>true or false are boolean values</li>
 * <li>Anything enclosed in brackets ([, ]) is parsed as a list. (Note: not yet supported)</li>
 * <li>Anything enclosed in "tags" (map[, ]) is parsed as a map. (Note: not yet supported)</li>
 * <li>Anything in curly brackets ({, }) is parsed as a set. (Note: not yet supported)</li>
 * </ul>
 * 
 * Examples:
 * 
 * <pre>
 * &#64;Initializer("'Hello'")
 * String getName();
 * 
 * &#64;Initializer("now()")
 * Date getTime();
 * 
 * &#64;Initializer("true")
 * boolean getActive();
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface Initializer {
	String value();
}
