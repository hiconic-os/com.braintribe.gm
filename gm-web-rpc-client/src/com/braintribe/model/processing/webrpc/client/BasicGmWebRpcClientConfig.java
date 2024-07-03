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
package com.braintribe.model.processing.webrpc.client;

import com.braintribe.model.processing.rpc.commons.api.config.GmRpcClientConfig;
import com.braintribe.transport.http.HttpClientProvider;
import com.braintribe.utils.stream.api.StreamPipeFactory;

public class BasicGmWebRpcClientConfig extends GmRpcClientConfig {

	private String url;
	private String contentType;
	private Long retryInterval;
	private Long callTimeout;
	private HttpClientProvider httpClientProvider;
	private Integer socketTimeout;
	private StreamPipeFactory streamPipeFactory;

	public Integer getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(Long retryInterval) {
		this.retryInterval = retryInterval;
	}

	public Long getCallTimeout() {
		return callTimeout;
	}

	public void setCallTimeout(Long callTimeout) {
		this.callTimeout = callTimeout;
	}

	public HttpClientProvider getHttpClientProvider() {
		return httpClientProvider;
	}

	public void setHttpClientProvider(HttpClientProvider httpClientProvider) {
		this.httpClientProvider = httpClientProvider;
	}

	public StreamPipeFactory getStreamPipeFactory() {
		return streamPipeFactory;
	}

	public void setStreamPipeFactory(StreamPipeFactory streamPipeFactory) {
		this.streamPipeFactory = streamPipeFactory;
	}

}
