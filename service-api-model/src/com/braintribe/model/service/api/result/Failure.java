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
package com.braintribe.model.service.api.result;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

public interface Failure extends ServiceResult {

	EntityType<Failure> T = EntityTypes.T(Failure.class);

	String getType();
	void setType(String type);

	String getMessage();
	void setMessage(String message);

	String getDetails();
	void setDetails(String details);
	
	String getTracebackId();
	void setTracebackId(String tracebackId);
	
	Failure getCause();
	void setCause(Failure cause);

	List<Failure> getSuppressed();
	void setSuppressed(List<Failure> suppressed);

	ServiceRequest getNotification();
	void setNotification(ServiceRequest value);

	@Override
	default ServiceResultType resultType() {
		return ServiceResultType.failure;
	}

	@Override
	default Failure asFailure() {
		return this;
	}

}
