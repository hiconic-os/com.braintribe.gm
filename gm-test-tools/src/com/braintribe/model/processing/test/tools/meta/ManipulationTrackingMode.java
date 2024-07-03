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
package com.braintribe.model.processing.test.tools.meta;

import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.model.processing.session.api.managed.ManipulationMode;

/**
 * @author peter.gazdik
 */
public enum ManipulationTrackingMode {

	/** Manipulations are not remotified at all. */
	LOCAL,

	/** Manipulations are remotified using PERSISTENCE references. */
	PERSISTENT,

	/** Manipulations are remotified using GLOBAL references. */
	GLOBAL;

	public ManipulationMode toManipulationMode() {
		switch (this) {
			case GLOBAL:
				return ManipulationMode.REMOTE_GLOBAL;
			case LOCAL:
				return ManipulationMode.LOCAL;
			case PERSISTENT:
				return ManipulationMode.REMOTE;
			default:
				throw new UnknownEnumException(this);
		}
	}

}
