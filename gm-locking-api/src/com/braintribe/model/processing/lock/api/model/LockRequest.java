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
package com.braintribe.model.processing.lock.api.model;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.PlatformRequest;

public interface LockRequest extends PlatformRequest {
	EntityType<LockRequest> T = EntityTypes.T(LockRequest.class);

	boolean getExclusive();
	void setExclusive(boolean exclusive);
	
	String getIdentification();
	void setIdentfication(String identification);
	
	String getHolderId();
	void setHolderId(String holderId);
	
	String getCallerSignature();
	void setCallerSignature(String callerSignature);
	
	String getMachineSignature();
	void setMachineSignature(String machineSignature);

}
