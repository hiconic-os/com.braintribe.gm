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
package com.braintribe.model.access.collaboration.distributed;

import static com.braintribe.model.access.collaboration.distributed.tools.CsaOperationBuilder.managePersistence;

import java.util.concurrent.locks.Lock;

import com.braintribe.model.cortexapi.access.collaboration.CollaborativePersistenceRequest;
import com.braintribe.model.cortexapi.access.collaboration.ReadOnlyCollaborativePersistenceRequest;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

/**
 * See {@link DistributedCollaborativeSmoodAccess} to understand how locking / consistency / performance is achieved.
 * 
 * @author peter.gazdik
 */
/* package */ class DistributedCollaborativeAccessManager implements ServiceProcessor<CollaborativePersistenceRequest, Object> {

	private final DistributedCollaborativeSmoodAccess access;
	private final Lock distributedLock;
	private final Lock writeLock;

	private final ServiceProcessor<CollaborativePersistenceRequest, Object> delegate;

	public DistributedCollaborativeAccessManager(DistributedCollaborativeSmoodAccess access) {
		this.access = access;
		this.writeLock = access.getLock().writeLock();
		this.distributedLock = access.distributedLock;
		this.delegate = access.collaborativeAccessManager;
	}

	@Override
	public Object process(ServiceRequestContext requestContext, CollaborativePersistenceRequest request) {
		if (isReadOnly(request))
			return processReadOnly(requestContext, request);
		else
			return processUpdate(requestContext, request);
	}

	private boolean isReadOnly(CollaborativePersistenceRequest request) {
		return request instanceof ReadOnlyCollaborativePersistenceRequest;
	}

	private Object processReadOnly(ServiceRequestContext requestContext, CollaborativePersistenceRequest request) {
		access.ensureUpToDate();

		return delegate.process(requestContext, request);
	}

	/**
	 * POSSIBLE OPTIMIZATION: see {@link DistributedCollaborativeSmoodAccess#applyManipulation}
	 */
	private Object processUpdate(ServiceRequestContext requestContext, CollaborativePersistenceRequest request) {
		// update before acquiring the distributed lock - if there is a big update, don't block the whole cluster
		access.ensureUpToDate();

		distributedLock.lock();
		try {

			writeLock.lock();
			try {
				return dw_processUpdate(requestContext, request);

			} finally {
				writeLock.unlock();
			}

		} finally {
			distributedLock.unlock();
		}
	}

	private Object dw_processUpdate(ServiceRequestContext requestContext, CollaborativePersistenceRequest request) {
		access.dw_ensureUpToDate();
		Object result = delegate.process(requestContext, request);
		access.dw_storeCsaOperation(managePersistence(request));

		return result;

	}

}
