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
package com.braintribe.model.bvd.math;

import com.braintribe.model.generic.annotation.Abstract;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import java.util.List;

import com.braintribe.model.generic.value.type.ImplicitlyTypedDescriptor;

/**
 * A {@link ImplicitlyTypedDescriptor} that performs arithmetic operation on a list of operands
 *
 */
@Abstract
public interface ArithmeticOperation extends ImplicitlyTypedDescriptor{

	final EntityType<ArithmeticOperation> T = EntityTypes.T(ArithmeticOperation.class);

	List<Object> getOperands(); // Date, DateOffset, TimeZoneOffset, TimeSpan, integer, long, float, double, decimal 
	void setOperands(List<Object> operands);
}
