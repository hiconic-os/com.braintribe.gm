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
package com.braintribe.model.query.functions;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.query.PropertyOperand;

/**
 * {@link PropertyFunction}s are functions that operate on properties and have a configurable {@link PropertyOperand}
 * 
 * @deprecated this (function for just a property) makes no sense, functions (in general) must work on operands of many different types
 */
@Deprecated
@Abstract
public interface PropertyFunction extends QueryFunction {

	EntityType<PropertyFunction> T = EntityTypes.T(PropertyFunction.class);

	void setPropertyOperand(PropertyOperand propertyOperand);
	PropertyOperand getPropertyOperand();
}
