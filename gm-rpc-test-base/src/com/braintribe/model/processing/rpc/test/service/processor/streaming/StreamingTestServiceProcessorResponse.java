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
package com.braintribe.model.processing.rpc.test.service.processor.streaming;

import java.util.Date;


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;


public interface StreamingTestServiceProcessorResponse extends StreamingResponse {

	final EntityType<StreamingTestServiceProcessorResponse> T = EntityTypes.T(StreamingTestServiceProcessorResponse.class);

	void setResource1(Resource resource);
	Resource getResource1();

	void setResource2(Resource resource);
	Resource getResource2();

	void setCapture1Md5(String captureMd5);
	String getCapture1Md5();

	void setCapture2Md5(String captureMd5);
	String getCapture2Md5();

	void setResponseDate(Date responseDate);
	Date getResponseDate();

}
