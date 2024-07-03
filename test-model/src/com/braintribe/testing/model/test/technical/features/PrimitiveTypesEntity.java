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
package com.braintribe.testing.model.test.technical.features;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * An entity with properties for all supported primitive types, e.g. <code>int</code> (instead of <code>Integer).
 *
 * @author michael.lafite
 *
 * @see SimpleTypesEntity
 */

public interface PrimitiveTypesEntity extends GenericEntity {

	EntityType<PrimitiveTypesEntity> T = EntityTypes.T(PrimitiveTypesEntity.class);

	boolean getPrimitiveBooleanProperty();
	void setPrimitiveBooleanProperty(boolean primitiveBooleanProperty);

	int getPrimitiveIntegerProperty();
	void setPrimitiveIntegerProperty(int primitiveIntegerProperty);

	long getPrimitiveLongProperty();
	void setPrimitiveLongProperty(long primitiveLongProperty);

	float getPrimitiveFloatProperty();
	void setPrimitiveFloatProperty(float primitiveFloatProperty);

	double getPrimitiveDoubleProperty();
	void setPrimitiveDoubleProperty(double primitiveDoubleProperty);
}
