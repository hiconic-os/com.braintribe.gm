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
package com.braintribe.model.processing.query.test.model.indexed;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Indexed;
import com.braintribe.model.generic.annotation.meta.Unique;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
public interface IndexedB extends GenericEntity, IsIndexed {

	EntityType<IndexedB> T = EntityTypes.T(IndexedB.class);

	String unique = "unique";
	String ambig = "ambig";
	String metric = "metric";

	/**
	 * This is unique, so {@link IndexedBSub} and {@link IndexedAB} cannot have the same value of this property.
	 * 
	 * @see IndexedA#getUnique()
	 */
	@Unique
	@Indexed
	String getUnique();
	void setUnique(String unique);

	@Indexed
	String getAmbig();
	void setAmbig(String ambig);

	@Indexed
	String getMetric();
	void setMetric(String metric);

	// @formatter:off
	@Override default void putAmbig(String ambig) { setAmbig(ambig); }
	@Override default void putUnique(String unique) { setUnique(unique); }
	@Override default void putMetric(String metric) { setMetric(metric); }
	// @formatter:on

}
