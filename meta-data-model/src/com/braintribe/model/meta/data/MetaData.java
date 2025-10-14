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
package com.braintribe.model.meta.data;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.selector.MetaDataSelector;

@Abstract
public interface MetaData extends GenericEntity {

	EntityType<MetaData> T = EntityTypes.T(MetaData.class);

	String conflictPriority = "conflictPriority";
	String inherited = "inherited";
	String important = "important";

	MetaDataSelector getSelector();
	void setSelector(MetaDataSelector selector);

	/** @see #getImportant() */
	Double getConflictPriority();
	void setConflictPriority(Double conflictPriority);

	/**
	 * Indicates that this MetaData is automatically inherited alongside the entity hierarchy (i.e. this is only relevant when resolving for entity
	 * type or a property.
	 */
	@Initializer("true")
	boolean getInherited();
	void setInherited(boolean inherited);

	/**
	 * Getter for the <tt>importance</tt> flag, which modifies the resolution priority of given meta data.
	 * <p>
	 * In normal situation, any meta-data defined on a given entity has higher priority than any meta-data coming from a super-type. Also, any
	 * meta-data on given property/enumConstant, has priority over meta-data coming from the owner (property MD may be defined for GmEntityTyp,
	 * enumConstant MD for GmEnumType. If, however, this flag is set to <tt>true</tt>, this MD is treated as if it was set directly on the item we are
	 * resolving for, and possible conflicts are only resolvable by {@link MetaData#setConflictPriority(Double) conflictPriority}.
	 * <p>
	 * This allows us to define e.g. property meta-data on some root type in a hierarchy, that are valid for all the properties of any entity in the
	 * hierarchy, and can only be overridden by means of higher conflictPriority.
	 * <p>
	 * For MD with the same hierarchy level (for entities) and same conflictPriority value a third criterion exists based on the model where they are
	 * defined. The closer to the one model given to MD resolver, the earlier the MD shows up. For example for entity types This means the MD defined
	 * on the actual type (as opposed to ones defined on overrides) has the lowest priority.
	 */
	boolean getImportant();
	void setImportant(boolean important);

}
