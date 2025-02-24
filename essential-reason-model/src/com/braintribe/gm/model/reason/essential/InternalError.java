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
package com.braintribe.gm.model.reason.essential;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.annotation.Transient;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface InternalError extends Reason {
	EntityType<InternalError> T = EntityTypes.T(InternalError.class);
	
	/**
	 * @return - an attached {@link Exception}, will not be persisted!
	 */
	@Transient
	Throwable getJavaException();
	void setJavaException(Throwable javaException);

	
	static InternalError from(Throwable t) {
		return from(t, t.getMessage());
	}
	
	static InternalError from(Throwable t, String text) {
		InternalError error = T.create();
		error.setJavaException(t);
		error.setText(text);
		return error;
	}
	
	static InternalError create(String text) {
		InternalError error = T.create();
		error.setText(text);
		return error;
	}
	
	@Override
	default Throwable linkedThrowable() {
		Throwable javaException = getJavaException();
		return javaException != null //
				? javaException //
				: getCreationStackTrace();
	}
}
