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

import java.util.concurrent.TimeUnit;

import org.apache.http.config.Registry;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class StaleSupportivePoolingHttpClientConnectionManager extends PoolingHttpClientConnectionManager {

	public StaleSupportivePoolingHttpClientConnectionManager() {
		super();
	}

	public StaleSupportivePoolingHttpClientConnectionManager(HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
		super(connFactory);
	}

	public StaleSupportivePoolingHttpClientConnectionManager(long timeToLive, TimeUnit tunit) {
		super(timeToLive, tunit);
	}

	public StaleSupportivePoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, DnsResolver dnsResolver) {
		super(socketFactoryRegistry, dnsResolver);
	}

	public StaleSupportivePoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory,
			DnsResolver dnsResolver) {
		super(socketFactoryRegistry, connFactory, dnsResolver);
	}

	public StaleSupportivePoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory,
			SchemePortResolver schemePortResolver, DnsResolver dnsResolver, long timeToLive, TimeUnit tunit) {
		super(socketFactoryRegistry, connFactory, schemePortResolver, dnsResolver, timeToLive, tunit);
	}

	public StaleSupportivePoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
		super(socketFactoryRegistry, connFactory);
	}

	public StaleSupportivePoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry) {
		super(socketFactoryRegistry);
	}
}
