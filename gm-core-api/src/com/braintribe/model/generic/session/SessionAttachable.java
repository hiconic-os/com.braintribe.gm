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
package com.braintribe.model.generic.session;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.GmSystemInterface;

@GmSystemInterface
@Deprecated
public interface SessionAttachable extends GenericEntity {

	/** @deprecated use {@link GenericEntity#attach(GmSession)} */
	@Deprecated
	void attachSession(GmSession session);

	/** @deprecated use {@link GenericEntity#detach()} */
	@Deprecated
	GmSession detachSession();

	/** @deprecated use {@link GenericEntity#session()} */
	@Deprecated
	GmSession accessSession();

}
