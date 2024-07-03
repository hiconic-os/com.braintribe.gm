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
package com.braintribe.testing.tools.gm.access;

import java.util.Collections;

import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.access.NonIncrementalAccess;
import com.braintribe.model.meta.GmMetaModel;

/**
 * A {@link NonIncrementalAccess} that doesn't actually persist any data.
 *
 * @author peter.gazdik
 */
public class TransientNonIncrementalAccess implements NonIncrementalAccess {

	private GmMetaModel metaModel;

	public TransientNonIncrementalAccess() {
		// nothing to do
	}

	public TransientNonIncrementalAccess(GmMetaModel metaModel) {
		this.metaModel = metaModel;
	}

	public void setMetaModel(GmMetaModel metaModel) {
		this.metaModel = metaModel;
	}

	@Override
	public GmMetaModel getMetaModel() {
		return metaModel;
	}

	@Override
	public Object loadModel() throws ModelAccessException {
		return Collections.emptySet();
	}

	@Override
	public void storeModel(Object model) throws ModelAccessException {
		// nothing to do
	}

}
