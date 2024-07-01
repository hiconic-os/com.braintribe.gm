// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.vde.evaluator.api;

import com.braintribe.model.processing.vde.evaluator.api.aspects.DateAspect;
import com.braintribe.model.processing.vde.evaluator.api.aspects.SessionAspect;
import com.braintribe.model.processing.vde.evaluator.api.aspects.VariableProviderAspect;

/**
 * An interface for describing various aspects (pieces of information) for evaluating value descriptors. This uses the
 * pattern to emulate extensible enumerations, where our constants are the class literals of sub-types of this
 * interface. For example, if our evaluator needs a date format (of type String), we could use a class like:
 * 
 * {@code  public interface DateAspect extends VdeContextAspect<String> ... }
 * 
 * @param <T>
 *            type for given aspect
 * 
 * @see DateAspect
 * @see SessionAspect
 * @see VariableProviderAspect
 * 
 */

public interface VdeContextAspect<T> {
	// this should stay empty
}
