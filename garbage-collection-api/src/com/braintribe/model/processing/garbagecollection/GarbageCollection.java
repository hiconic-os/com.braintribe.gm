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
package com.braintribe.model.processing.garbagecollection;

import java.util.List;

import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * The <code>GarbageCollection</code> deletes entities that are no longer needed. Usually that's the case, if they are
 * not reachable from one of "root" entities (i.e. entities that must not be deleted by GC).
 *
 * @author michael.lafite
 */
public interface GarbageCollection {

	/**
	 * Performs the garbage collection on the specified <code>subsets</code> using the passed <code>session</code> and
	 * returns a report.
	 *
	 * @param session
	 *            the session through which the data will be accessed.
	 * @param subsets
	 *            the list of subsets to perform the GC on.
	 * @param testModeEnabled
	 *            If enabled, the actual deletion of entities will be skipped.
	 *
	 * @throws GarbageCollectionException
	 *             if any error occurs.
	 */
	GarbageCollectionReport performGarbageCollection(PersistenceGmSession session, List<SubsetConfiguration> subsets,
			boolean testModeEnabled) throws GarbageCollectionException;

}
