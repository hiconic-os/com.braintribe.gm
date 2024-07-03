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

import java.lang.reflect.Proxy;

import com.braintribe.logging.Logger;

public class GmWebRpcClient extends GmWebRpcClientBase {

	private static final Logger logger = Logger.getLogger(GmWebRpcClient.class);

	protected GmWebRpcClient(GmWebRpcClientConfig config) {
		setConfig(config);
	}

	public static void close(Object proxy) {
		GmWebRpcClient clientToClose = null;

		if (proxy instanceof GmWebRpcClient) {
			clientToClose = (GmWebRpcClient) proxy;
		} else {
			clientToClose = (GmWebRpcClient) Proxy.getInvocationHandler(proxy);
		}
		clientToClose.close();
	}

	@Override
	protected Logger logger() {
		return logger;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [serviceInterface=" + (serviceInterface == null ? "null" : serviceInterface.getName()) + ", serviceId=" + serviceId + ", url=" + getUrl() + "]";
	}

	protected void setConfig(GmWebRpcClientConfig config) {

		super.setConfig(config);

		this.serviceId = config.getServiceId();
		this.serviceInterface = config.getServiceInterface();

		logClientConfiguration(logger, false);

	}

}
