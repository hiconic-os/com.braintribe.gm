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
package com.braintribe.model.processing.test.itw.entity;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Confidential;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
public interface EssentialMdEntity extends GenericEntity {

	EntityType<EssentialMdEntity> T = EntityTypes.T(EssentialMdEntity.class);

	String regularProperty = "regularProperty";
	String confidential = "confidential";

	String getRegularProperty();
	void setRegularProperty(String regularProperty);

	@Confidential
	String getConfidential();
	void setConfidential(String confidential);

	/**
	 * This property is declared confidential in the {@link EssentialMdSiblingEntity} to test the multiple inheritance case. The sub-type
	 * {@link EssentialMdSubEntity} must have both "confidential" and "siblingConfidential" marked as confidential.
	 */
	String getSiblingConfidential();
	void setSiblingConfidential(String siblingConfidential);

}
