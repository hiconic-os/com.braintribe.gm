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
package com.braintribe.model.processing.meta.cmd.extended;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.Predicate;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;

/**
 * @author peter.gazdik
 */
@Abstract
public interface MdDescriptor extends MetaData, QualifiedMetaData {

	EntityType<MdDescriptor> T = EntityTypes.T(MdDescriptor.class);

	MetaData getResolvedValue();
	void setResolvedValue(MetaData resolvedValue);

	/** The model on which this meta data was declared. Only null if the MD is configured as default. */
	GmMetaModel getOwnerModel();
	void setOwnerModel(GmMetaModel ownerModel);

	boolean getResolvedAsDefault();
	void setResolvedAsDefault(boolean resolvedAsDefault);

	/** If given MD is a {@link Predicate}, this is it's corresponding boolean value. */
	boolean getIsTrue();
	void setIsTrue(boolean isTrue);

	@Override
	default MetaData metaData() {
		return this; // This is QualifiedMetaData so that it is compatible with MetaDataBox
	}

	@Override
	default GmModelElement ownerElement() {
		return null; // no need for this value, the MdDescriptor contains the owner element information
	}

	default String origin() {
		return getResolvedAsDefault() ? "[default]" : getOwnerModel().getName();
	}
	
}
