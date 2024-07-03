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

import com.braintribe.cc.lcd.HashSupportWrapperCodec;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;

/**
 * 
 */
public class LocalEntityHashSupportWrapperCodec extends HashSupportWrapperCodec<LocalEntityProperty> {

	public static final LocalEntityHashSupportWrapperCodec INSTANCE = new LocalEntityHashSupportWrapperCodec();
	
	@Override
	protected int entityHashCode(LocalEntityProperty e) {
		final int prime = 31;
		int result = 1;

		result = prime * result + e.getPropertyName().hashCode();
		result = prime * result + e.getEntity().hashCode();
		
		return result;
	}

	@Override
	protected boolean entityEquals(LocalEntityProperty e1, LocalEntityProperty e2) {
		return e1.getEntity() == e2.getEntity() && e1.getPropertyName().equals(e2.getPropertyName());
	}

	
}
