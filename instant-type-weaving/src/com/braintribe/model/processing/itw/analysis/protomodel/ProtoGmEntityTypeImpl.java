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
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmTypeKind;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.weaving.ProtoGmEntityType;
import com.braintribe.model.weaving.ProtoGmProperty;
import com.braintribe.model.weaving.ProtoGmType;
import com.braintribe.model.weaving.override.ProtoGmPropertyOverride;

/**
 * Pseudo-implementation of {@link GmEntityType}
 * 
 * @see GenericEntity_pseudo
 * 
 * @author peter.gazdik
 */
public class ProtoGmEntityTypeImpl extends ProtoGmCustomTypeImpl implements ProtoGmEntityType {

	private List<ProtoGmEntityType> superTypes;
	private List<ProtoGmProperty> properties;
	private List<ProtoGmPropertyOverride> propertyOverrides;
	private Set<MetaData> metaData;
	private boolean isAbstract;
	private ProtoGmType evaluatesTo;

	@Override
	public List<ProtoGmEntityType> getSuperTypes() {
		return superTypes;
	}

	public void setSuperTypes(List<ProtoGmEntityType> superTypes) {
		this.superTypes = superTypes;
	}

	@Override
	public List<ProtoGmProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<ProtoGmProperty> properties) {
		this.properties = properties;
	}

	@Override
	public List<ProtoGmPropertyOverride> getPropertyOverrides() {
		return propertyOverrides;
	}

	public void setPropertyOverrides(List<ProtoGmPropertyOverride> propertyOverrides) {
		this.propertyOverrides = propertyOverrides;
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
	public boolean getIsAbstract() {
		return isAbstract;
	}

	@Override
	public void setIsAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	@Override
	public ProtoGmType getEvaluatesTo() {
		return evaluatesTo;
	}

	public void setEvaluatesTo(ProtoGmType evaluatesTo) {
		this.evaluatesTo = evaluatesTo;
	}

	@Override
	public GmTypeKind typeKind() {
		return GmTypeKind.ENTITY;
	}

}
