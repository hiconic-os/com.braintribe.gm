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

import com.braintribe.gwt.utils.genericmodel.EntitiesFinder;

/**
 * Holds all info/tools required to perform a {@link GarbageCollection GarbageCollection} on a particular subset of the
 * set of persisted entities.
 *
 * @author michael.lafite
 */
public class SubsetConfiguration {

	private String subsetId;

	private EntitiesFinder rootEntitiesFinder;

	private EntitiesFinder subsetEntitiesFinder;

	public SubsetConfiguration(final String subsetId, final EntitiesFinder rootEntitiesFinder,
			final EntitiesFinder subsetEntitiesFinder) {
		setSubsetId(subsetId);
		setRootEntitiesFinder(rootEntitiesFinder);
		setSubsetEntitiesFinder(subsetEntitiesFinder);
	}

	public String getSubsetId() {
		return this.subsetId;
	}

	public void setSubsetId(final String subsetId) {
		this.subsetId = subsetId;
	}

	public EntitiesFinder getRootEntitiesFinder() {
		return this.rootEntitiesFinder;
	}

	public void setRootEntitiesFinder(final EntitiesFinder rootEntitiesFinder) {
		this.rootEntitiesFinder = rootEntitiesFinder;
	}

	public EntitiesFinder getSubsetEntitiesFinder() {
		return this.subsetEntitiesFinder;
	}

	public void setSubsetEntitiesFinder(final EntitiesFinder subsetEntitiesFinder) {
		this.subsetEntitiesFinder = subsetEntitiesFinder;
	}

}
