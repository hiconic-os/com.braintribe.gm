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

import com.braintribe.model.generic.pseudo.GenericEntity_pseudo;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.weaving.ProtoGmEntityType;
import com.braintribe.model.weaving.ProtoGmProperty;
import com.braintribe.model.weaving.ProtoGmType;
import com.braintribe.model.weaving.restriction.ProtoGmTypeRestriction;

/**
 * Pseudo-implementation of {@link GmProperty}
 * 
 * @see GenericEntity_pseudo
 * 
 * @author peter.gazdik
 */
public class ProtoGmPropertyImpl extends ProtoGmPropertyInfoImpl implements ProtoGmProperty {

	private ProtoGmType type;
	private ProtoGmTypeRestriction typeRestriction;
	private String name;
	private boolean nullable = true; // Later this should be replaced with default-value annotation (once supported)
	private ProtoGmEntityType entityType;

	@Override
	public ProtoGmType getType() {
		return type;
	}

	public void setType(ProtoGmType type) {
		this.type = type;
	}

	@Override
	public ProtoGmTypeRestriction getTypeRestriction() {
		return typeRestriction;
	}

	public void setTypeRestriction(ProtoGmTypeRestriction typeRestriction) {
		this.typeRestriction = typeRestriction;
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
	public boolean getNullable() {
		return nullable;
	}

	@Override
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public ProtoGmEntityType getDeclaringType() {
		return entityType;
	}

	public void setDeclaringType(ProtoGmEntityType entityType) {
		this.entityType = entityType;
	}

}
