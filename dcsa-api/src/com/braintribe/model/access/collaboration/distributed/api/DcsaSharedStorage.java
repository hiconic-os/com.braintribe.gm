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
package com.braintribe.model.access.collaboration.distributed.api;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import com.braintribe.model.access.collaboration.distributed.api.model.CsaOperation;
import com.braintribe.model.access.collaboration.distributed.api.model.CsaStoreResource;
import com.braintribe.model.resource.Resource;

/**
 * Shared storage for the DistributedCollaborativeSmoodAccess. It allows reading and writing of {@link CsaOperation}s. <br>
 * As the methods imply, the implementation is also responsible for managing so called "markers", i.e. string identifiers of the operations, which are
 * used by the client to communicate which operations have already been read.
 */
public interface DcsaSharedStorage {

	Lock getLock(String accessId);

	/**
	 * @return marker of the persisted operation
	 */
	String storeOperation(String accessId, CsaOperation csaOperation);

	/**
	 * Returns a {@link DcsaIterable} instance, which binds two things - all the {@link CsaOperation}s from the storage with a marker more recent than
	 * given {@code lastReadMarker}, and the latest marker (i.e. marker of the last operation retrieved from the iterable).
	 * 
	 * Note that if given marker is <tt>null</tt> it indicates that all the operations should be returned. Additionally, if there are no operations
	 * returned, the returned marker MUST be <tt>null</tt>.
	 */
	DcsaIterable readOperations(String accessId, String lastReadMarker);

	/**
	 * Reads the actual {@link CsaStoreResource#getPayload() resource} (binary data) corresponding to the given
	 * {@link CsaStoreResource#getResourceRelativePath() paths}. This method is used for every {@link CsaStoreResource} returned from
	 * {@link #readOperations(String, String)} which doesn't have the binary data attached (i.e. payload is <tt>null</tt>).
	 * <p>
	 * This is the recommended approach as downloading resources eagerly might significantly impact performance (especially bootstrap time of a new
	 * instance that is doing a bulk update).
	 */
	default Map<String, Resource> readResource(String accessId, Collection<String> storedResourcesPaths) {
		throw new RuntimeException(
				getClass().getName() + " does not support lazy-loading of resources. AccessId: " + accessId + ", paths: " + storedResourcesPaths);
	}

}
