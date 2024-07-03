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
package com.braintribe.model.processing.rpc.commons.impl.crypto;

import java.security.Key;

import com.braintribe.model.processing.rpc.commons.api.GmRpcException;
import com.braintribe.model.processing.rpc.commons.api.crypto.RpcServerCryptoContext;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * @deprecated Cryptographic capabilities were removed from the RPC layer. This interface is now obsolete and will be
 *             removed in a future version.
 */
@Deprecated
public class NoOpRpcServerCryptoContext implements RpcServerCryptoContext {

	public static final RpcServerCryptoContext INSTANCE = new NoOpRpcServerCryptoContext();

	private NoOpRpcServerCryptoContext() {
	}

	@Override
	public boolean encryptResponseFor(ServiceRequest request) {
		return false;
	}

	@Override
	public CryptoResponseContext createResponseContext(String clientId) throws GmRpcException {
		throw new UnsupportedOperationException("Method not supported");
	}

	@Override
	public Key generateKey() throws GmRpcException {
		throw new UnsupportedOperationException("Method not supported");
	}

	@Override
	public String exportKey(String clientId, Key key) throws GmRpcException {
		throw new UnsupportedOperationException("Method not supported");
	}

}
