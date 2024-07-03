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
package com.braintribe.model.processing.core.commons;

import com.braintribe.cc.lcd.HashingComparator;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;

/**
 * 
 */
public class LocalEntityPropertyComparator implements HashingComparator<LocalEntityProperty> {

	public final static LocalEntityPropertyComparator INSTANCE = new LocalEntityPropertyComparator();

	@Override
	public boolean compare(LocalEntityProperty o1, LocalEntityProperty o2) {
		return o1 == o2 || //
				o1.getEntity() == o2.getEntity() && o1.getPropertyName().equals(o2.getPropertyName());
	}

	@Override
	public int computeHash(LocalEntityProperty lep) {
		return 31 * lep.getEntity().hashCode() + lep.getPropertyName().hashCode();
	}

}
