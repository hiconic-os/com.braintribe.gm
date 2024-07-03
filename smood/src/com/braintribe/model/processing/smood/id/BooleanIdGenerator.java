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

import java.util.concurrent.locks.ReentrantLock;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.processing.smood.IdGenerator;

public class BooleanIdGenerator implements IdGenerator<Boolean> {

	private boolean trueUsed = false;
	private boolean falseUsed = false;
	private ReentrantLock lock = new ReentrantLock();

	@Override
	public Boolean generateId(GenericEntity entity) {
		lock.lock();
		try {
			if (!falseUsed) {
				falseUsed = true;
				return false;
			}

			if (!trueUsed) {
				trueUsed = true;
				return true;
			}
			throw new GenericModelException("BooleanIdGenerator cannot generate id. All (i.e. both) values already used!");
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void recognizeUsedId(Boolean id) {
		lock.lock();
		try {
			if (id) {
				trueUsed = true;

			} else {
				falseUsed = true;
			}
		} finally {
			lock.unlock();
		}
	}
}
