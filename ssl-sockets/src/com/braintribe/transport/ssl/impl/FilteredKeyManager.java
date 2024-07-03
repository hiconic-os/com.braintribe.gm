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
package com.braintribe.transport.ssl.impl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

import com.braintribe.cfg.Configurable;


public class FilteredKeyManager implements X509KeyManager {

	protected String clientAlias = null;
	protected String serverAlias = null;

	protected final X509KeyManager delegate;

	public FilteredKeyManager(X509KeyManager originatingKeyManager) {
		this.delegate = originatingKeyManager;
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		return this.delegate.getCertificateChain(alias);
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		return this.delegate.chooseClientAlias(keyType, issuers, socket);
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		return this.delegate.chooseServerAlias(keyType, issuers, socket);
	}

	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		if (this.clientAlias == null) {
			return this.delegate.getClientAliases(keyType, issuers);
		} else {
			return new String[] {this.clientAlias};
		}
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		return this.delegate.getPrivateKey(alias);
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		if (this.serverAlias == null) {
			return this.delegate.getServerAliases(keyType, issuers);
		} else {
			return new String[] {this.serverAlias};
		}
	}

	@Configurable
	public void setClientAlias(String clientAlias) {
		this.clientAlias = clientAlias;
	}

	@Configurable
	public void setServerAlias(String serverAlias) {
		this.serverAlias = serverAlias;
	}


}
