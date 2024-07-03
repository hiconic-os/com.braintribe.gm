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

public interface RpcConstants {

	final String RPC_META_SESSIONID = "sessionId";
	final String RPC_META_THREADNAME = "threadName";
	final String RPC_META_NDC = "ndc";
	final String RPC_META_NODEID = "nodeId";

	final String RPC_MAPKEY_REQUEST  = "rpc-request";
	final String RPC_MAPKEY_RESPONSE = "rpc-response";
	final String RPC_MAPKEY_REQUESTED_ENDPOINT = "rpc-requestedEndpoint";
	final String RPC_MAPKEY_REQUESTOR_ADDRESS = "rpc-requestorAddress";

	final String RPC_LOGSTEP_UNMARSHALL_REQUEST = "Unmarshalling of request";
	final String RPC_LOGSTEP_MARSHALL_REQUEST = "Marshalling of request";
	final String RPC_LOGSTEP_UNMARSHALL_RESPONSE = "Unmarshalling of response";
	final String RPC_LOGSTEP_MARSHALL_RESPONSE = "Marshalling of response";
	final String RPC_LOGSTEP_KEY_IMPORT = "Importing request key";
	final String RPC_LOGSTEP_INITIAL = "Request";
	final String RPC_LOGSTEP_FINAL = "Requested";

}
