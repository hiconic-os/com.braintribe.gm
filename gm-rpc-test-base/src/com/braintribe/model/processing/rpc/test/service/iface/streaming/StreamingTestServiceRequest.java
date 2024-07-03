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
package com.braintribe.model.processing.rpc.test.service.iface.streaming;

import java.util.Date;

import com.braintribe.model.generic.GenericEntity;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.CallStreamCapture;
import com.braintribe.model.resource.Resource;


public interface StreamingTestServiceRequest extends GenericEntity {

	final EntityType<StreamingTestServiceRequest> T = EntityTypes.T(StreamingTestServiceRequest.class);

	void setResource1(Resource resource1);
	Resource getResource1();

	void setResource2(Resource resource2);
	Resource getResource2();

	void setCapture1(CallStreamCapture capture);
	CallStreamCapture getCapture1();

	void setCapture2(CallStreamCapture capture);
	CallStreamCapture getCapture2();

	void setRequestDate(Date requestDate);
	Date getRequestDate();

}
