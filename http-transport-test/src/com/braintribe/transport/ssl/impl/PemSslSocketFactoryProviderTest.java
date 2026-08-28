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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.net.ssl.SSLContext;

import org.junit.Test;

public class PemSslSocketFactoryProviderTest {

	private static final String CERTIFICATE = "-----BEGIN CERTIFICATE-----\n"
			+ "MIIBiTCCAS+gAwIBAgIUWRg9oKxVGNgN7XxtncszdCWM03wwCgYIKoZIzj0EAwIw\n"
			+ "GjEYMBYGA1UEAwwPbXRscy10ZXN0LmxvY2FsMB4XDTI2MDgyODA5NDgzOFoXDTM2\n"
			+ "MDgyNTA5NDgzOFowGjEYMBYGA1UEAwwPbXRscy10ZXN0LmxvY2FsMFkwEwYHKoZI\n"
			+ "zj0CAQYIKoZIzj0DAQcDQgAEg/5AoD+dtO4prD1t9sdcYqbrDxHrKu+vEyN7VlTN\n"
			+ "pbMFl1j5EEwTuzdu2tKV4IWxwKHYfmQ7R6GvJZkRS8EVjaNTMFEwHQYDVR0OBBYE\n"
			+ "FE0QtbI6G94MFauJRi1K1XIPbceGMB8GA1UdIwQYMBaAFE0QtbI6G94MFauJRi1K\n"
			+ "1XIPbceGMA8GA1UdEwEB/wQFMAMBAf8wCgYIKoZIzj0EAwIDSAAwRQIgRfsDxdou\n"
			+ "iTT2QUU/5R7cpDp1Oe6BVVFQPiGugKHtY6kCIQCuv+VKXYgjsdjXopBlz5RfAMQI\n"
			+ "GWMLILWFF0otYw5G0A==\n"
			+ "-----END CERTIFICATE-----\n";

	private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n"
			+ "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgJDKGq/6GNJ0ChXKR\n"
			+ "GtSuHJBS/EJiywwAF2gxtIZ+fqahRANCAASD/kCgP5207imsPW32x1xipusPEesq\n"
			+ "768TI3tWVM2lswWXWPkQTBO7N27a0pXghbHAodh+ZDtHoa8lmRFLwRWN\n"
			+ "-----END PRIVATE KEY-----\n";

	private static final String OTHER_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n"
			+ "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgwTuMAZX8vh8zGA3K\n"
			+ "eNkPWxaiabusjSkpS4Y64/DV27+hRANCAASnS/m/6Xevbb4HlxG3fwwhqb8RFX8z\n"
			+ "Dqxi9w8onGo6y9f111nK75Z4dki6M1iZPMSJQPsIk9PauMgnMvkIf8GK\n"
			+ "-----END PRIVATE KEY-----\n";

	@Test
	public void buildsClientSslContextFromCanonicalPem() throws Exception {
		SSLContext context = provider(CERTIFICATE, PRIVATE_KEY).provideSSLContext();

		assertThat(context.getSocketFactory()).isNotNull();
	}

	@Test
	public void normalizesFlattenedAndBodyOnlyCertificateValues() throws Exception {
		String flattened = CERTIFICATE.replace('\n', ' ');
		String bodyOnly = CERTIFICATE.replace("-----BEGIN CERTIFICATE-----", "")
				.replace("-----END CERTIFICATE-----", "").replaceAll("\\s", "");

		assertThat(provider(flattened, PRIVATE_KEY).provideSSLContext().getSocketFactory()).isNotNull();
		assertThat(provider(bodyOnly, PRIVATE_KEY).provideSSLContext().getSocketFactory()).isNotNull();
	}

	@Test
	public void rejectsPrivateKeyWhichDoesNotBelongToCertificate() {
		assertThatThrownBy(() -> provider(CERTIFICATE, OTHER_PRIVATE_KEY).provideSSLContext())
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("does not belong");
	}

	private static PemSslSocketFactoryProvider provider(String certificate, String privateKey) {
		PemSslSocketFactoryProvider provider = new PemSslSocketFactoryProvider();
		provider.setCertificatePem(certificate);
		provider.setPrivateKeyPem(privateKey);
		return provider;
	}
}
