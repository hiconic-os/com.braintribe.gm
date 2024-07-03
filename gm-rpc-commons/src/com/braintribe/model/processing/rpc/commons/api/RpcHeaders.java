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
package com.braintribe.model.processing.rpc.commons.api;

public enum RpcHeaders {
	
	/*
	 * tells the version of the rpc protocol. If this is not given the version is 1
	 */
	rpcVersion("gm-rpc-version"),
	
	/*
	 * indicates whether the request or response body contains an unmarshallable RPC body.
	 */
	rpcBody("gm-rpc-body"),

	/*
	 * indicates the type of the marshalled generic model entity 
	 * contained in the body of a RPC request or response.
	 */
	rpcBodyType("gm-rpc-body-type"),

	/*
	 * the id of the requesting client 
	 */
	rpcClientId("gm-rpc-client-id"),

	/*
	 * the symmetric key used for encrypting the response, encrypted with the clients public key.
	 */
	rpcResponseKey("gm-rpc-response-key"),

	/*
	 * the algorithm of the symmetric key used for encrypting the response.
	 */
	rpcResponseKeyAlgorithm("gm-rpc-response-key-algo"),

	/*
	 * a flag that controls if the call occurs reasoned or not (true|false)
	 */
	rpcReasoning("gm-rpc-reasoning");
	
	private String headerName;
	
	private RpcHeaders(String headerName) {
		this.headerName = headerName;
	}
	
	public String getHeaderName() {
		return headerName;
	}

}
