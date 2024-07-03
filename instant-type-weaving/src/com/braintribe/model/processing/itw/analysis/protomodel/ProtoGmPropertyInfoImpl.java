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

import java.util.Set;

import com.braintribe.model.generic.pseudo.GenericEntity_pseudo;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.weaving.info.ProtoGmPropertyInfo;

/**
 * Pseudo-implementation of {@link GmProperty}
 * 
 * @see GenericEntity_pseudo
 * 
 * @author peter.gazdik
 */
public abstract class ProtoGmPropertyInfoImpl extends GenericEntity_pseudo implements ProtoGmPropertyInfo {

	private Set<MetaData> metaData;
	private Object initializer;

	@Override
	public Set<MetaData> getMetaData() {
		return metaData;
	}

	@Override
	public void setMetaData(Set<MetaData> metaData) {
		this.metaData = metaData;
	}

	@Override
	public Object getInitializer() {
		return initializer;
	}

	@Override
	public void setInitializer(Object initializer) {
		this.initializer = initializer;
	}

}
