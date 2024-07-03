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
package com.braintribe.model.meta.data.fulltext;


import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.PropertyMetaData;

/**
 * Indicates whether a property is part of the FullText store.
 * 
 * @author gunther.schenk
 */

public interface FulltextProperty extends PropertyMetaData, FulltextMetaData {

	EntityType<FulltextProperty> T = EntityTypes.T(FulltextProperty.class);

	// @formatter:off
	boolean getIndexed();
	void setIndexed(boolean indexed);

	boolean getStored();
	void setStored(boolean stored);

	TraversingCriterion getIndexingCriterion();
	void setIndexingCriterion(TraversingCriterion indexingCriterion);
	// @formatter:on

}
