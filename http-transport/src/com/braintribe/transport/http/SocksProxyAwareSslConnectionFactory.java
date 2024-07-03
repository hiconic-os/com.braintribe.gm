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
package com.braintribe.transport.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

public class SocksProxyAwareSslConnectionFactory extends SSLConnectionSocketFactory {

	private InetSocketAddress socksAddress = null;

	public SocksProxyAwareSslConnectionFactory(final SSLContext sslContext, final HostnameVerifier hostnameVerifier) {
		super(sslContext, hostnameVerifier);

		String socksHost = DefaultHttpClientProvider.getProperty("tf_socksProxyHost");
		String socksPort = DefaultHttpClientProvider.getProperty("tf_socksProxyPort");
		if (socksHost != null && socksPort != null) {
			socksAddress = new InetSocketAddress(socksHost, Integer.parseInt(socksPort));
		}

	}

	@Override
	public Socket createSocket(final HttpContext context) throws IOException {
		if (socksAddress == null) {
			return super.createSocket(context);
		}
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksAddress);
		return new Socket(proxy);
	}
}
