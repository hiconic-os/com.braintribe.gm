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
package com.braintribe.model.processing.webrpc.client.test;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.processing.rpc.commons.api.config.GmRpcClientConfig;
import com.braintribe.model.processing.rpc.test.commons.DataSize;
import com.braintribe.model.processing.rpc.test.commons.TestContext;
import com.braintribe.model.processing.rpc.test.commons.TestContextBuilder;
import com.braintribe.model.processing.rpc.test.service.processor.basic.BasicTestServiceProcessorRequest;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.DownloadCaptureTestServiceProcessorRequest;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.DownloadResourceTestServiceProcessorRequest;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.StreamingTestServiceProcessorRequest;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.UploadTestServiceProcessorRequest;
import com.braintribe.model.processing.webrpc.client.BasicGmWebRpcClientConfig;
import com.braintribe.model.processing.webrpc.client.GmWebRpcEvaluator;
import com.braintribe.model.service.api.CompositeRequest;

/**
 * <p>
 * Test suite using the DDSA evaluator {@link com.braintribe.model.processing.webrpc.client.GmWebRpcEvaluator} to
 * execute requests against the DDSA server {@link com.braintribe.model.processing.webrpc.server.GmWebRpcServer}.
 * 
 * <p>
 * The tests implementation as well as the implementation of the test services are packaged in the {@code GmRpcTestBase}
 * artifact.
 * 
 */
public class EvaluatorClientTest extends WebRpcTestBase {

	// ============================= //
	// =========== TESTS =========== //
	// ============================= //

	// == BasicTestServiceProcessor == //

	@Test
	public void testStandardBasicServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(BasicTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testStandardBasicServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(BasicTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testReAuthorizingBasicServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(BasicTestServiceProcessorRequest.T, true);
	}

	@Test
	public void testReAuthorizingBasicServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(BasicTestServiceProcessorRequest.T, true);
	}

	// == StreamingTestServiceProcessor == //

	@Test
	public void testStandardStreamingServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(StreamingTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testStandardStreamingServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(StreamingTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testReAuthorizingStreamingServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(StreamingTestServiceProcessorRequest.T, true);
	}

	@Test
	public void testReAuthorizingStreamingServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(StreamingTestServiceProcessorRequest.T, true);
	}

	// == UploadTestServiceProcessor == //

	@Test
	public void testStandardUploadServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(UploadTestServiceProcessorRequest.T, false);
	}

	@Test
	public void test100MegUploadServiceProcessorRequest() throws Exception {
		TestContext context = TestContextBuilder.create().set(DataSize.class, 100 * 1024 * 1024);
		testServiceProcessorRequest(context, UploadTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testStandardUploadServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(UploadTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testReAuthorizingUploadServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(UploadTestServiceProcessorRequest.T, true);
	}

	@Test
	public void testReAuthorizingUploadServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(UploadTestServiceProcessorRequest.T, true);
	}

	// == DownloadResourceTestServiceProcessor == //

	@Test
	public void testStandardDownloadResourceServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(DownloadResourceTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testStandardDownloadResourceServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadResourceTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testReAuthorizingDownloadResourceServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(DownloadResourceTestServiceProcessorRequest.T, true);
	}

	@Test
	public void testReAuthorizingDownloadResourceServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadResourceTestServiceProcessorRequest.T, true);
	}

	// == DownloadCaptureTestServiceProcessor == //

	@Test
	public void testStandardDownloadCaptureServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(DownloadCaptureTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testStandardDownloadCaptureServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadCaptureTestServiceProcessorRequest.T, false);
	}

	@Test
	public void testReAuthorizingDownloadCaptureServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(DownloadCaptureTestServiceProcessorRequest.T, true);
	}

	@Test
	public void testReAuthorizingDownloadCaptureServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadCaptureTestServiceProcessorRequest.T, true);
	}

	@Test
	public void testStandardDownloadCaptureServiceProcessorRequestNotifyingCache() throws Exception {
		with(DownloadCaptureTestServiceProcessorRequest.T).notifyEagerResponse().test();
	}

	// == CompositeTestServiceProcessor == //

	@Test
	public void testStandardCompositeServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(CompositeRequest.T, false);
	}

	@Test
	public void testStandardCompositeServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(CompositeRequest.T, false);
	}

	@Test
	public void testReAuthorizingCompositeServiceProcessorRequest() throws Exception {
		testServiceProcessorRequest(CompositeRequest.T, true);
	}

	@Test
	public void testReAuthorizingCompositeServiceProcessorRequestAsync() throws Exception {
		testServiceProcessorRequestAsync(CompositeRequest.T, true);
	}

	// ======================================== //
	// =========== SEQUENCIAL TESTS =========== //
	// ======================================== //

	// == BasicTestServiceProcessor == //

	@Test
	public void testStandardBasicServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(BasicTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testStandardBasicServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(BasicTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingBasicServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(BasicTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingBasicServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(BasicTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	// == StreamingTestServiceProcessor == //

	@Test
	public void testStandardStreamingServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(StreamingTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testStandardStreamingServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(StreamingTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingStreamingServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(StreamingTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingStreamingServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(StreamingTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	// == UploadTestServiceProcessor == //

	@Test
	public void testStandardUploadServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(UploadTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testStandardUploadServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(UploadTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingUploadServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(UploadTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingUploadServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(UploadTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	// == DownloadResourceTestServiceProcessor == //

	@Test
	public void testStandardDownloadResourceServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(DownloadResourceTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testStandardDownloadResourceServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadResourceTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingDownloadResourceServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(DownloadResourceTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingDownloadResourceServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadResourceTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	// == DownloadCaptureTestServiceProcessor == //

	@Test
	public void testStandardDownloadCaptureServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(DownloadCaptureTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testStandardDownloadCaptureServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadCaptureTestServiceProcessorRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingDownloadCaptureServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(DownloadCaptureTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingDownloadCaptureServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadCaptureTestServiceProcessorRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	// == CompositeTestServiceProcessor == //

	@Test
	public void testStandardCompositeServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(CompositeRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testStandardCompositeServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(CompositeRequest.T, false, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingCompositeServiceProcessorRequestSequential() throws Exception {
		testServiceProcessorRequest(CompositeRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	@Test
	public void testReAuthorizingCompositeServiceProcessorRequestSequentialAsync() throws Exception {
		testServiceProcessorRequestAsync(CompositeRequest.T, true, false, MAX_SEQUENTIAL_TESTS);
	}

	// ======================================== //
	// =========== CONCURRENT TESTS =========== //
	// ======================================== //

	// == BasicTestServiceProcessor == //

	@Test
	public void testStandardBasicServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(BasicTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testStandardBasicServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(BasicTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingBasicServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(BasicTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingBasicServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(BasicTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	// == StreamingTestServiceProcessor == //

	@Test
	public void testStandardStreamingServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(StreamingTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testStandardStreamingServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(StreamingTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingStreamingServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(StreamingTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingStreamingServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(StreamingTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	// == UploadTestServiceProcessor == //

	@Test
	public void testStandardUploadServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(UploadTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testStandardUploadServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(UploadTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingUploadServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(UploadTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingUploadServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(UploadTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	// == DownloadResourceTestServiceProcessor == //

	@Test
	public void testStandardDownloadResourceServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(DownloadResourceTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testStandardDownloadResourceServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadResourceTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingDownloadResourceServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(DownloadResourceTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingDownloadResourceServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadResourceTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	// == DownloadCaptureTestServiceProcessor == //

	@Test
	public void testStandardDownloadCaptureServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(DownloadCaptureTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testStandardDownloadCaptureServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadCaptureTestServiceProcessorRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingDownloadCaptureServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(DownloadCaptureTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingDownloadCaptureServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(DownloadCaptureTestServiceProcessorRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	// == CompositeTestServiceProcessor == //

	@Test
	public void testStandardCompositeServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(CompositeRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testStandardCompositeServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(CompositeRequest.T, false, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingCompositeServiceProcessorRequestConcurrent() throws Exception {
		testServiceProcessorRequest(CompositeRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	@Test
	public void testReAuthorizingCompositeServiceProcessorRequestConcurrentAsync() throws Exception {
		testServiceProcessorRequestAsync(CompositeRequest.T, true, true, MAX_CONCURRENT_TESTS);
	}

	// ============================================ //
	// =========== ERROR HANDLING TESTS =========== //
	// ============================================ //

	public void testAuthorizationFailure() {
		testUnauthorizedEvaluatorRequest();
	}

	@Test(expected = InterruptedException.class)
	public void testException() throws Throwable {
		try {
			testFailedEvaluatorRequest(InterruptedException.class);
		} catch (RuntimeException e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getClass().getName() + " has no cause", e.getCause());
			boolean unexpectedCause = (e.getCause() instanceof RuntimeException || e.getCause() instanceof Error);
			Assert.assertFalse(e.getClass().getName() + " is wrapping an unchecked exception: " + e.getCause(), unexpectedCause);
			throw e.getCause();
		} catch (Exception e) {
			Assert.fail("Unexpected exception: " + e);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRuntimeException() throws Exception {
		testFailedEvaluatorRequest(IllegalArgumentException.class);
	}

	// ============================= //
	// ========= COMMONS =========== //
	// ============================= //

	@Override
	public GmWebRpcEvaluator createService(GmRpcClientConfig clientConfig) {
		BasicGmWebRpcClientConfig config = (BasicGmWebRpcClientConfig) clientConfig;
		config.setUrl(rpcUrl.toString());
		GmWebRpcEvaluator evaluator = new GmWebRpcEvaluator();
		evaluator.setConfig(config);
		return evaluator;
	}

	@Override
	public <S> void destroyService(S service) {
		// no-op
	}

}
