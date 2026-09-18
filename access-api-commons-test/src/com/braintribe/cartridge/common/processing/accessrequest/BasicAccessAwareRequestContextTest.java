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
package com.braintribe.cartridge.common.processing.accessrequest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.braintribe.cartridge.common.processing.accessrequest.model.ResourceCarryingRequest;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.TransientSource;
import com.braintribe.testing.tools.gm.GmTestTools;

/**
 * Tests for {@link BasicAccessAwareRequestContext}.
 * <p>
 * We test that transient properties (for {@link Resource}s) are handled correctly when merging the request to the internal session.
 */
public class BasicAccessAwareRequestContextTest {

	private static final String ACCESS_ID = "test.access";
	private static final String PAYLOAD = "transient payload";

	/** Every session is a new memory-only one. Nothing is committed here, the test is only about what the merge produces. */
	private final PersistenceGmSessionFactory sessionFactory = accessId -> GmTestTools.newSessionWithSmoodAccessMemoryOnly();

	@Test
	public void resourceOfRequestIsStillStreamableAfterMerge() {
		ResourceCarryingRequest originalRequest = requestWithTransientResource();

		ResourceCarryingRequest request = newContext(originalRequest).getRequest();

		assertMerged(originalRequest, request);
		assertThat(contentOf(request.getResource())).isEqualTo(PAYLOAD);
	}

	@Test
	public void resourceOfSystemRequestIsStillStreamableAfterMerge() {
		ResourceCarryingRequest originalRequest = requestWithTransientResource();

		ResourceCarryingRequest systemRequest = newContext(originalRequest).getSystemRequest();

		assertMerged(originalRequest, systemRequest);
		assertThat(contentOf(systemRequest.getResource())).isEqualTo(PAYLOAD);
	}

	/** Both sides of the context see their own copy, and each of them can be read. */
	@Test
	public void requestAndSystemRequestAreIndependentAndBothReadable() {
		ResourceCarryingRequest originalRequest = requestWithTransientResource();
		BasicAccessAwareRequestContext<ResourceCarryingRequest> context = newContext(originalRequest);

		ResourceCarryingRequest request = context.getRequest();
		ResourceCarryingRequest systemRequest = context.getSystemRequest();

		assertThat(request).isNotSameAs(systemRequest);
		assertThat(contentOf(request.getResource())).isEqualTo(PAYLOAD);
		assertThat(contentOf(systemRequest.getResource())).isEqualTo(PAYLOAD);

		// The original is untouched and still readable too.
		assertThat(contentOf(originalRequest.getResource())).isEqualTo(PAYLOAD);
	}

	// ###############################################
	// ## . . . . . . . . Assertions . . . . . . . .##
	// ###############################################

	/** The merged request is a copy, not the original, and its Resource still knows how to read its data. */
	private static void assertMerged(ResourceCarryingRequest originalRequest, ResourceCarryingRequest mergedRequest) {
		assertThat(mergedRequest).isNotSameAs(originalRequest);

		Resource resource = mergedRequest.getResource();
		assertThat(resource).isNotNull();
		assertThat(resource).isNotSameAs(originalRequest.getResource());
		assertThat(resource.getResourceSource()).isInstanceOf(TransientSource.class);
		assertThat(resource.isStreamable()).isTrue();
	}

	// ###############################################
	// ## . . . . . . . . Helpers . . . . . . . . . ##
	// ###############################################

	private BasicAccessAwareRequestContext<ResourceCarryingRequest> newContext(ResourceCarryingRequest originalRequest) {
		return new BasicAccessAwareRequestContext<>(null, sessionFactory, sessionFactory, originalRequest);
	}

	private static ResourceCarryingRequest requestWithTransientResource() {
		ResourceCarryingRequest result = ResourceCarryingRequest.T.create();
		result.setDomainId(ACCESS_ID);
		result.setResource(transientResource());

		return result;
	}

	private static Resource transientResource() {
		byte[] data = PAYLOAD.getBytes(StandardCharsets.UTF_8);

		Resource result = Resource.createTransient(() -> new ByteArrayInputStream(data));
		result.setName("payload.txt");

		return result;
	}

	private static String contentOf(Resource resource) {
		try (InputStream in = resource.openStream()) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
