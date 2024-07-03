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
package com.braintribe.model.processing.rpc.test.service.iface.basic;

import java.util.Date;

public class BasicTestServiceImpl implements BasicTestService {

	@Override
	public BasicTestServiceResponse test(BasicTestServiceRequest request) throws Exception {

		BasicTestServiceResponse response = BasicTestServiceResponse.T.create();

		response = test(request, response);

		return response;
	}

	@Override
	public BasicTestServiceResponseEncrypted testEncrypted(BasicTestServiceRequest request) throws Exception {

		BasicTestServiceResponseEncrypted response = BasicTestServiceResponseEncrypted.T.create();

		response = test(request, response);

		return response;

	}

	private static <T extends BasicTestServiceResponse> T test(BasicTestServiceRequest request, T response) throws Exception {

		response.setRequestId(request.getRequestId());
		response.setResponseDate(new Date());

		return response;

	}

}
