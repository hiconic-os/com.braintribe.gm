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
package com.braintribe.model.access;

import java.util.Collections;
import java.util.function.Supplier;

import com.braintribe.model.meta.GmMetaModel;

/**
 * A {@link NonIncrementalAccess} that doesn't actually persist any data.
 * 
 * It can be used either as a base for an actual implementation, or as an actual value, serving the role of a transient
 * {@link NonIncrementalAccess} (one that does not load/store anything).
 *
 * @author peter.gazdik
 */
public class EmptyNonIncrementalAccess implements NonIncrementalAccess {

	private Supplier<GmMetaModel> metaModelSupplier;

	public EmptyNonIncrementalAccess() {
		// nothing to do
	}

	public EmptyNonIncrementalAccess(GmMetaModel metaModel) {
		this(() -> metaModel);
	}

	public EmptyNonIncrementalAccess(Supplier<GmMetaModel> metaModelSupplier) {
		this.metaModelSupplier = metaModelSupplier;
	}

	public void setMetaModel(GmMetaModel metaModel) {
		setMetaModelSupplier(() -> metaModel);
	}

	public void setMetaModelSupplier(Supplier<GmMetaModel> metaModelSupplier) {
		this.metaModelSupplier = metaModelSupplier;
	}

	@Override
	public GmMetaModel getMetaModel() {
		return metaModelSupplier.get();
	}

	@Override
	public Object loadModel() throws ModelAccessException {
		return Collections.emptySet(); // just in case the caller expects a non-null value
	}

	@Override
	public void storeModel(Object model) throws ModelAccessException {
		// nothing to do
	}

}
