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
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmTypeKind;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.weaving.ProtoGmEnumConstant;
import com.braintribe.model.weaving.ProtoGmEnumType;

/**
 * Pseudo-implementation of {@link GmEnumType}
 * 
 * @see GenericEntity_pseudo
 * 
 * @author peter.gazdik
 */
public class ProtoGmEnumTypeImpl extends ProtoGmCustomTypeImpl implements ProtoGmEnumType {

	private List<ProtoGmEnumConstant> constants;
	private Set<MetaData> metaData;
	private Set<MetaData> enumConstantMetaData;

	@Override
	public List<ProtoGmEnumConstant> getConstants() {
		return constants;
	}

	public void setConstants(List<ProtoGmEnumConstant> constants) {
		this.constants = constants;
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
	public Set<MetaData> getEnumConstantMetaData() {
		return enumConstantMetaData;
	}

	@Override
	public void setEnumConstantMetaData(Set<MetaData> enumConstantMetaData) {
		this.enumConstantMetaData = enumConstantMetaData;
	}

	@Override
	public GmTypeKind typeKind() {
		return GmTypeKind.ENUM;
	}
	
	@Override
	public boolean isGmEnum() {
		return true;
	}


}
