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

import com.braintribe.model.processing.rpc.test.commons.StreamingTools.RandomDataStore;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceProcessorException;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

public class DownloadCaptureTestServiceProcessor
		implements ServiceProcessor<DownloadCaptureTestServiceProcessorRequest, DownloadCaptureTestServiceProcessorResponse> {

	@Override
	public DownloadCaptureTestServiceProcessorResponse process(ServiceRequestContext requestContext,
			DownloadCaptureTestServiceProcessorRequest request) {

		DownloadCaptureTestServiceProcessorResponse response = DownloadCaptureTestServiceProcessorResponse.T.create();

		if (request.getRespondEagerly()) {
			response.setEager(true);
			requestContext.notifyResponse(response);
		}

		try {
			RandomDataStore.download(request.getCapture(), request.getResourceId());
		} catch (Exception e) {
			throw new ServiceProcessorException("Failed to download resource to capture: " + e.getMessage(), e);
		}

		if (request.getRespondEagerly()) {
			response = DownloadCaptureTestServiceProcessorResponse.T.create();
		}

		return response;

	}

}
