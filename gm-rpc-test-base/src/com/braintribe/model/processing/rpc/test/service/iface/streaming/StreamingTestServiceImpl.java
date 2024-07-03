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

import com.braintribe.model.processing.rpc.test.commons.StreamingTools;
import com.braintribe.model.resource.Resource;

public class StreamingTestServiceImpl implements StreamingTestService {

	@Override
	public StreamingTestServiceResponse test(StreamingTestServiceRequest request) throws Exception {

		StreamingTestServiceResponse response = StreamingTestServiceResponse.T.create();

		response = test(request, response);

		return response;

	}

	@Override
	public StreamingTestServiceResponseEncrypted testEncrypted(StreamingTestServiceRequest request) throws Exception {

		StreamingTestServiceResponseEncrypted response = StreamingTestServiceResponseEncrypted.T.create();

		response = test(request, response);

		return response;

	}

	private static <T extends StreamingTestServiceResponse> T test(StreamingTestServiceRequest request, T response) throws Exception {

		Resource resource1 = request.getResource1();
		Resource resource2 = request.getResource2();

		StreamingTools.checkResource(resource1);
		StreamingTools.checkResource(resource2);

		response.setCapture1Md5(StreamingTools.serveCapture(request.getCapture1()));
		response.setCapture2Md5(StreamingTools.serveCapture(request.getCapture2()));

		response.setResource1(StreamingTools.createResource());
		response.setResource2(StreamingTools.createResource());

		response.setResponseDate(new Date());

		return response;

	}

}
