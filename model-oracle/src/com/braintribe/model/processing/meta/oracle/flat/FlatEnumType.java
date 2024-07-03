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
package com.braintribe.model.processing.meta.oracle.flat;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.info.GmEnumTypeInfo;

/**
 * @author peter.gazdik
 */
public class FlatEnumType extends FlatCustomType<GmEnumType, GmEnumTypeInfo> {

	private volatile Map<String, FlatEnumConstant> flatEnumConstant;
	private ReentrantLock flatEnumConstantsLock = new ReentrantLock();

	public FlatEnumType(GmEnumType type, FlatModel flatModel) {
		super(type, flatModel);
	}

	@Override
	public boolean isEntity() {
		return false;
	}

	public Map<String, FlatEnumConstant> acquireFlatEnumConstants() {
		if (flatEnumConstant == null) {
			flatEnumConstantsLock.lock();
			try {
				if (flatEnumConstant == null) {
					flatEnumConstant = FlatConstantsFactory.buildFor(this);
				}
			} finally {
				flatEnumConstantsLock.unlock();
			}
		}

		return flatEnumConstant;
	}

}
