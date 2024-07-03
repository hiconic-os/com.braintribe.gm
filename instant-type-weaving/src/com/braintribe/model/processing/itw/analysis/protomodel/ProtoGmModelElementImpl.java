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
package com.braintribe.model.processing.itw.analysis.protomodel;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.weaving.ProtoGmModelElement;

/**
 * Very special class used as a base when we need classes for our entities. One special example is InstantTypeWeaving
 * which needs instances of {@link GmMetaModel} (and co.) as input for actual weaving, so we simply need instances of
 * some entities before we weave the first entity.
 * 
 * NOTE that this base class only supports the two special properties from {@link GenericEntity} and not any other of
 * the inherited methods. Those are also not intended to be implemented in the future.
 * 
 * @author peter.gazdik
 */
public abstract class ProtoGmModelElementImpl implements ProtoGmModelElement {

	private Object id;
	private String partition;
	private String globalId;

	public <T> T getId() {
		return (T) id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}

	@Override
	public String getGlobalId() {
		return globalId;
	}

	@Override
	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

}
