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

public class DoubleIdGenerator implements IdGenerator<Double> {

	private double maxId = 0d;

	private static final double HUNDRED_TRILLION = 100d * 1000d * 1000d * 1000d * 1000d;
	private static final double HUNDRED_TRILLIONTH = 1d / HUNDRED_TRILLION;
	private ReentrantLock lock = new ReentrantLock();

	@Override
	public Double generateId(GenericEntity entity) {
		return generateId();
	}

	protected Double generateId() {
		if (maxId == Double.MAX_VALUE) {
			throw new GenericModelException("DoubleIdGenerator cannot generate id. MAX_VALUE already reached!");
		}

		lock.lock();
		try {
			return maxId = findNextId(maxId);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * In JVM it would be ideal to use {@link Math#nextUp(double)}, but we would still need an emulation in GWT.
	 */
	private static double findNextId(double id) {
		double originalId = id;

		/* This first number should be such, that if added to id, the value will be changed and is as small as possible. Only
		 * for super small (in absolute value) number we have a backup, cause the first number could be zero. HUNDRED_TRILLIONTH
		 * (10^-14) is a number that when added to 1.0d makes a different number (in fact, 10^-15 does too, but this is OK as
		 * well) */
		double diff = Math.max(Math.abs(id) / HUNDRED_TRILLION, HUNDRED_TRILLIONTH);

		while (true) {
			double newId = id + diff;
			if (newId != id) {
				if (newId < 0f && originalId > 0.f) {
					// overflow
					return Float.MAX_VALUE;
				}

				return newId;
			}

			diff *= 2d;

			if (diff < 0f) {
				// this should be unreachable
				return Float.MAX_VALUE;
			}
		}
	}

	@Override
	public void recognizeUsedId(Double id) {
		lock.lock();
		try {
			maxId = Math.max(id, maxId);
		} finally {
			lock.unlock();
		}
	}

}
