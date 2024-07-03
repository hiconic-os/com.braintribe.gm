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
package com.braintribe.model.processing.query.eval.api.function;

/**
 * An interface for describing various aspects (pieces of information) for evaluating query functions. This uses the
 * pattern to emulate extensible enumerations, where our constants are the class literals of sub-types of this
 * interface. For example, if our function needs a date format (of type String), we could use a class like:
 * 
 * {@code  public interface DateFormatAspect extends QueryFunctionAspect<String> ... }
 * 
 * @param <T>
 *            Type of a given aspect.
 */
public interface QueryFunctionAspect<T> {
	// empty
}
