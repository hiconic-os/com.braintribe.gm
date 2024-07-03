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
package com.braintribe.model.processing.rpc.test.wire.contract;

import java.util.List;

import javax.servlet.Filter;

import com.braintribe.model.processing.webrpc.server.GmWebRpcServer;
import com.braintribe.wire.api.context.WireContext;

public interface WebRpcTestContract extends RpcTestContract {

	static WireContext<WebRpcTestContract> context() {
		// @formatter:off
		WireContext<WebRpcTestContract> wireContext = 
				com.braintribe.wire.api.Wire
					.context(WebRpcTestContract.class)
						.bindContracts(WebRpcTestContract.class.getName().replace(".contract."+WebRpcTestContract.class.getSimpleName(), ""))
					.build();
		return wireContext;
		// @formatter:on
	}
	
	GmWebRpcServer server();

	List<Filter> filters();

}
