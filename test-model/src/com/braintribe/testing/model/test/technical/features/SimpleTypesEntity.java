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

import java.math.BigDecimal;
import java.util.Date;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.SimpleType;

/**
 * An entity with properties for all {@link SimpleType}s, but no collections or relations to other entities.
 *
 * @author michael.lafite
 *
 * @see PrimitiveTypesEntity
 * @see ComplexEntity
 */

public interface SimpleTypesEntity extends GenericEntity {

	EntityType<SimpleTypesEntity> T = EntityTypes.T(SimpleTypesEntity.class);

	String getStringProperty();
	void setStringProperty(String stringProperty);

	Boolean getBooleanProperty();
	void setBooleanProperty(Boolean booleanProperty);

	Integer getIntegerProperty();
	void setIntegerProperty(Integer integerProperty);

	Long getLongProperty();
	void setLongProperty(Long longProperty);

	Float getFloatProperty();
	void setFloatProperty(Float floatProperty);

	Double getDoubleProperty();
	void setDoubleProperty(Double doubleProperty);

	Date getDateProperty();
	void setDateProperty(Date dateProperty);

	BigDecimal getDecimalProperty();
	void setDecimalProperty(BigDecimal decimalProperty);

}
