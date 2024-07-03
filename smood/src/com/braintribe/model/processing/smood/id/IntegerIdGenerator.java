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
package com.braintribe.model.processing.smood.id;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.processing.smood.IdGenerator;

public class IntegerIdGenerator implements IdGenerator<Integer> {

	private int maxId = 0;

	@Override
	public Integer generateId(GenericEntity entity) {
		return generateId();
	}

	protected Integer generateId() {
		if (maxId == Integer.MAX_VALUE) {
			throw new GenericModelException("IntegerIdGenerator cannot generate id. MAX_VALUE already reached!");
		}

		return ++maxId;
	}

	@Override
	public void recognizeUsedId(Integer id) {
		maxId = Math.max(id, maxId);
	}

}
