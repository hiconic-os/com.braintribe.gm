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
package com.braintribe.model.processing.rpc.commons.api.crypto;

import java.security.Key;

import com.braintribe.crypto.Decryptor;
import com.braintribe.model.processing.rpc.commons.api.GmRpcException;

/**
 * <p>
 * Cryptography context for RPC clients.
 * 
 * @deprecated Cryptographic capabilities were removed from the RPC layer. This interface is now obsolete and will be
 *             removed in a future version.
 */
@Deprecated
public interface RpcClientCryptoContext extends RpcCryptoContext {

	/**
	 * <p>
	 * Returns a unique identifier for the RPC caller.
	 * 
	 * @return A unique identifier for the RPC caller.
	 */
	String getClientId();

	/**
	 * <p>
	 * Returns a {@link Decryptor} for for decrypting the RPC response based on the given key data.
	 * 
	 * @param encodedKey
	 *            The encoded key
	 * @param keyAlgorithm
	 *            The algorithm of the encoded key
	 * @return A {@link Decryptor} for for decrypting the RPC response
	 * @throws GmRpcException
	 *             In case a {@link Decryptor} fails to be created
	 */
	Decryptor responseDecryptor(String encodedKey, String keyAlgorithm) throws GmRpcException;

	/**
	 * <p>
	 * Returns a {@link Key} for decrypting the RPC response based on the given key data.
	 * 
	 * @param encodedKey
	 *            The encoded key
	 * @param keyAlgorithm
	 *            The algorithm of the encoded key
	 * @return A {@link Key} for decrypting the RPC response
	 * @throws GmRpcException
	 *             In case a {@link Key} fails to be imported
	 */
	Key importKey(String encodedKey, String keyAlgorithm) throws GmRpcException;

}
