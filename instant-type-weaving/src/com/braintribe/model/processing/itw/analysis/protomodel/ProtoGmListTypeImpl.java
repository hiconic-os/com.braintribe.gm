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
import com.braintribe.model.meta.GmListType;
import com.braintribe.model.meta.GmTypeKind;
import com.braintribe.model.weaving.ProtoGmListType;
import com.braintribe.model.weaving.ProtoGmType;

/**
 * Pseudo-implementation of {@link GmListType}
 * 
 * @see GenericEntity_pseudo
 * 
 * @author peter.gazdik
 */
public class ProtoGmListTypeImpl extends ProtoGmTypeImpl implements ProtoGmListType {

	private ProtoGmType elementType;

	@Override
	public ProtoGmType getElementType() {
		return elementType;
	}

	public void setElementType(ProtoGmType elementType) {
		this.elementType = elementType;
	}

	@Override
	public GmTypeKind typeKind() {
		return GmTypeKind.LIST;
	}

}
