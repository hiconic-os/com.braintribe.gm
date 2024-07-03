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
package com.braintribe.model.bvd.convert;

import java.math.BigDecimal;


import com.braintribe.model.generic.value.type.DecimalDescriptor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A {@link FormattedConvert} that converts to decimal ({@link BigDecimal})
 *
 */

public interface ToDecimal extends FormattedConvert, DecimalDescriptor {

	final EntityType<ToDecimal> T = EntityTypes.T(ToDecimal.class);
	// can only convert from String, Boolean
}
