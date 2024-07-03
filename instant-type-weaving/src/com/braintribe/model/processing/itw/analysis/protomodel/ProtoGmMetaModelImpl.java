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

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.pseudo.GenericEntity_pseudo;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.weaving.ProtoGmMetaModel;
import com.braintribe.model.weaving.ProtoGmType;
import com.braintribe.model.weaving.override.ProtoGmCustomTypeOverride;
import com.braintribe.processing.async.api.AsyncCallback;

/**
 * Pseudo-implementation of {@link GmMetaModel}
 * 
 * @see GenericEntity_pseudo
 * 
 * @author peter.gazdik
 */
public class ProtoGmMetaModelImpl extends GenericEntity_pseudo implements ProtoGmMetaModel {

	private Set<ProtoGmType> types;
	private Set<ProtoGmCustomTypeOverride> typeOverrides;
	private Set<MetaData> metaData;
	private String name;
	private String version;
	private List<ProtoGmMetaModel> dependencies;

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public Set<ProtoGmType> getTypes() {
		return types;
	}

	public void setTypes(Set<ProtoGmType> types) {
		this.types = types;
	}

	@Override
	public Set<ProtoGmCustomTypeOverride> getTypeOverrides() {
		return typeOverrides;
	}

	public void setTypeOverrides(Set<ProtoGmCustomTypeOverride> typeOverrides) {
		this.typeOverrides = typeOverrides;
	}

	@Override
	public Set<MetaData> getMetaData() {
		return metaData;
	}

	@Override
	public void setMetaData(Set<MetaData> metaData) {
		this.metaData = metaData;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<ProtoGmMetaModel> getDependencies() {
		return dependencies;
	}

	public void setDependencie(List<ProtoGmMetaModel> dependencies) {
		this.dependencies = dependencies;
	}

	@Override
	public void deploy() {
		throw new UnsupportedOperationException("Method 'ProtoGmMetaModelImpl.deploy' is not supported!");
	}

	@Override
	public void deploy(AsyncCallback<Void> asyncCallack) {
		throw new UnsupportedOperationException("Method 'ProtoGmMetaModelImpl.deploy' is not supported!");
	}

}
