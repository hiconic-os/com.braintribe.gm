// ============================================================================
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
package com.braintribe.testing.tools.gm.resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.resource.ResourceAccess;
import com.braintribe.model.processing.session.api.resource.ResourceAccessFactory;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.ResourceSource;

/**
 * Creates an {@link InMemoryResourceAccess} for a session, and keeps the binary data of every {@link Resource}.
 * <p>
 * One factory stands for the storage of one access. Use the very same instance for every session on that access, otherwise a session cannot read what
 * another session has written.
 *
 * @see com.braintribe.testing.tools.gm.GmTestTools#attachInMemoryResources
 */
public class InMemoryResourceAccessFactory implements ResourceAccessFactory<PersistenceGmSession> {

	private final Map<String, byte[]> dataBySourceId = new ConcurrentHashMap<>();

	@Override
	public ResourceAccess newInstance(PersistenceGmSession session) {
		return new InMemoryResourceAccess(session, dataBySourceId);
	}

	/** The number of stored binaries. Useful for an assertion that nothing was uploaded twice, or that data was deleted. */
	public int storedBinaryCount() {
		return dataBySourceId.size();
	}

	/** The stored data of given {@link ResourceSource}, or null if there is none. */
	public byte[] dataOfSource(String sourceId) {
		return dataBySourceId.get(sourceId);
	}

	/** The stored data of given {@link Resource}, or null if there is none. */
	public byte[] dataOf(Resource resource) {
		ResourceSource source = resource == null ? null : resource.getResourceSource();

		return source == null ? null : dataOfSource(source.getId());
	}

	/** Forgets all stored data, without touching the entities in the access. */
	public void clear() {
		dataBySourceId.clear();
	}

}
