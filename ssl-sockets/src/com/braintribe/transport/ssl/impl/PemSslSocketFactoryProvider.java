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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;
import com.braintribe.transport.ssl.SslSocketFactoryProvider;

/**
 * Provides an {@link SSLContext} which presents a client certificate during the TLS handshake, i.e. the client side of mutual TLS. In contrast
 * to {@link VersatileSslSocketFactoryProvider} the key material is not read from a key store file but taken directly as PEM text, which makes
 * it configurable from wherever the surrounding configuration system can carry a string - a YAML file, an environment variable, a mounted
 * secret or a secret manager.
 * <p>
 * The key store holding that material is assembled in memory and never touches the file system. Its password is a throwaway value which never
 * leaves this instance, which is why no password has to be configured.
 */
public class PemSslSocketFactoryProvider implements SslSocketFactoryProvider {

	protected static Logger logger = Logger.getLogger(PemSslSocketFactoryProvider.class);

	private static final String KEY_ENTRY_ALIAS = "client";

	/** Private key algorithms tried in order - the PEM header of a PKCS#8 key does not tell which one it is. */
	private static final List<String> KEY_ALGORITHMS = Arrays.asList("RSA", "EC");

	/** One PEM block, tolerating a body which lost its line breaks somewhere on its way through the configuration. */
	private static final Pattern PEM_BLOCK = Pattern.compile("-----BEGIN ([A-Za-z0-9 ]+)-----(.*?)-----END \\1-----", Pattern.DOTALL);

	protected static final int BASE64_LINE_LENGTH = 64;

	protected String certificatePem = null;
	protected String privateKeyPem = null;
	protected String keyManagerFactoryAlgorithm = Constants.DEFAULT_KEY_MANAGER_FACTORY_ALGORITHM;

	protected boolean trustAll = false;

	protected String securityProtocol = Constants.DEFAULT_SECURITY_PROTOCOL;

	/** One random password per instance, shared by the key store entry and the key manager factory. */
	private char[] password;

	@Override
	public SSLSocketFactory provideSSLSocketFactory() throws Exception {
		return provideSSLContext().getSocketFactory();
	}

	@Override
	public SSLContext provideSSLContext() throws Exception {
		KeyStore keyStore = createKeyStore();

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(this.keyManagerFactoryAlgorithm);
		kmf.init(keyStore, throwawayPassword());

		SSLContext sc = SSLContext.getInstance(this.securityProtocol);
		sc.init(kmf.getKeyManagers(), trustManagers(), null);

		return sc;
	}

	/**
	 * The in-memory PKCS#12 store holding the certificate chain and the matching private key. A key store entry requires a password, so a
	 * random one is used - it is only ever seen by {@link #provideSSLContext()}.
	 */
	protected KeyStore createKeyStore() throws Exception {
		List<Certificate> chain = parseCertificateChain();
		PrivateKey privateKey = parsePrivateKey();

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, null);

		try {
			keyStore.setKeyEntry(KEY_ENTRY_ALIAS, privateKey, throwawayPassword(), chain.toArray(new Certificate[0]));
		} catch (KeyStoreException e) {
			throw new IllegalStateException("The configured client certificate was rejected. The private key has to belong to the first "
					+ "certificate of the chain, and each further certificate has to be the issuer of the one before it, "
					+ "i.e. the leaf certificate comes first.", e);
		}

		return keyStore;
	}

	/** Parses the configured certificate PEM, which may hold a whole chain with the leaf certificate first. */
	protected List<Certificate> parseCertificateChain() throws Exception {
		if (isBlank(this.certificatePem))
			throw new IllegalStateException("No client certificate configured.");

		String pem = normalizePem(this.certificatePem);

		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		Collection<? extends Certificate> certificates = certificateFactory
				.generateCertificates(new ByteArrayInputStream(pem.getBytes(StandardCharsets.US_ASCII)));

		if (certificates.isEmpty())
			throw new IllegalStateException("The configured client certificate does not contain any X.509 certificate. "
					+ "It is expected in PEM form, i.e. including the BEGIN/END CERTIFICATE lines.");

		List<Certificate> chain = new ArrayList<>(certificates);

		if (logger.isDebugEnabled() && chain.get(0) instanceof X509Certificate) {
			X509Certificate leaf = (X509Certificate) chain.get(0);
			logger.debug("Using client certificate " + leaf.getSubjectX500Principal() + " (valid until " + leaf.getNotAfter() + ")");
		}

		return chain;
	}

	/**
	 * Rewrites the PEM blocks of the given text into canonical form: each marker on a line of its own and the base64 body wrapped at
	 * {@value #BASE64_LINE_LENGTH} characters. The certificate parser of the JDK works line based, while a configuration value may well have
	 * travelled through a single-line carrier - an environment variable fed from a secret field which collapses newlines, for instance - so a
	 * flattened PEM would not be recognized without this.
	 * <p>
	 * If the text carries no BEGIN/END markers at all it is taken to be the bare base64 body of a single certificate and gets wrapped
	 * accordingly. The {@link #parsePrivateKey() private key} needs none of this, since there the whitespace is discarded before decoding
	 * anyway.
	 */
	protected static String normalizePem(String pem) {
		StringBuilder normalized = new StringBuilder();

		Matcher matcher = PEM_BLOCK.matcher(pem);
		boolean anyBlockFound = false;
		while (matcher.find()) {
			anyBlockFound = true;
			appendPemBlock(normalized, matcher.group(1), matcher.group(2));
		}

		if (anyBlockFound)
			return normalized.toString();

		String body = pem.replaceAll("\\s", "");
		if (body.isEmpty())
			return pem;

		appendPemBlock(normalized, "CERTIFICATE", body);

		return normalized.toString();
	}

	private static void appendPemBlock(StringBuilder normalized, String label, String rawBody) {
		String body = rawBody.replaceAll("\\s", "");

		normalized.append("-----BEGIN ").append(label).append("-----\n");
		for (int i = 0; i < body.length(); i += BASE64_LINE_LENGTH)
			normalized.append(body, i, Math.min(i + BASE64_LINE_LENGTH, body.length())).append('\n');
		normalized.append("-----END ").append(label).append("-----\n");
	}

	/**
	 * Decodes an unencrypted PKCS#8 PEM key. An OpenSSL PKCS#1 key (BEGIN RSA PRIVATE KEY) or an encrypted key has to be converted first:
	 *
	 * <pre>
	 * openssl pkcs8 -topk8 -nocrypt -in client.key -out client-pkcs8.key
	 * </pre>
	 */
	protected PrivateKey parsePrivateKey() throws Exception {
		if (isBlank(this.privateKeyPem))
			throw new IllegalStateException("No client private key configured.");

		String base64 = this.privateKeyPem.replaceAll("-----(BEGIN|END)[^-]*-----", "").replaceAll("\\s", "");
		if (base64.isEmpty())
			throw new IllegalStateException("The configured client private key does not contain any payload between its BEGIN/END lines.");

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64));

		for (String algorithm : KEY_ALGORITHMS) {
			try {
				return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
			} catch (Exception ignored) {
				// try the next algorithm
			}
		}

		throw new IllegalStateException("The configured client private key is neither an RSA nor an EC key in unencrypted PKCS#8 format. "
				+ "Convert it with: openssl pkcs8 -topk8 -nocrypt -in client.key -out client-pkcs8.key");
	}

	/**
	 * Trust managers for validating the <i>server</i> side. Returning <tt>null</tt> lets the SSLContext use the default trust store of the JVM,
	 * which is what we want unless {@link #setTrustAll(boolean) trustAll} disables validation altogether.
	 */
	protected TrustManager[] trustManagers() {
		if (!this.trustAll)
			return null;

		return new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				// Intentionally left empty
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				// Intentionally left empty
			}
		} };
	}

	private char[] throwawayPassword() {
		if (this.password == null) {
			byte[] bytes = new byte[32];
			new SecureRandom().nextBytes(bytes);
			this.password = Base64.getEncoder().encodeToString(bytes).toCharArray();
		}
		return this.password;
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	@Configurable
	public void setCertificatePem(String certificatePem) {
		this.certificatePem = certificatePem;
	}

	@Configurable
	public void setPrivateKeyPem(String privateKeyPem) {
		this.privateKeyPem = privateKeyPem;
	}

	@Configurable
	public void setKeyManagerFactoryAlgorithm(String keyManagerFactoryAlgorithm) {
		this.keyManagerFactoryAlgorithm = keyManagerFactoryAlgorithm;
	}

	@Configurable
	public void setTrustAll(boolean trustAll) {
		this.trustAll = trustAll;
	}

	public String getSecurityProtocol() {
		return securityProtocol;
	}

	@Configurable
	public void setSecurityProtocol(String securityProtocol) {
		this.securityProtocol = securityProtocol;
	}

	@Override
	public String toString() {
		return "PemSslSocketFactoryProvider with an inline client certificate, trustAll: " + trustAll;
	}
}
