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
package com.braintribe.model.notification;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;


/**
 * Classification of a message.
 * 
 */
public enum Level implements EnumBase {
	/** ERROR is a message level indicating a serious failure. */
	ERROR,
	/** WARNING is a message level indicating a potential problem. */
	WARNING,
	/** SUCCESS is a message level for indicating a successfull operation. */
	SUCCESS,
	/** INFO is a message level for informational messages. */
	INFO;

	public static final EnumType<Level> T = EnumTypes.T(Level.class);
	
	@Override
	public EnumType<Level> type() {
		return T;
	}

}
