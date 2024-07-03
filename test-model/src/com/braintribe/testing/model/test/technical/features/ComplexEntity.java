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

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A complex entity (compared to {@link SimpleEntity} with more properties and relations to other entities.
 *
 * @author michael.lafite
 */

public interface ComplexEntity extends GenericEntity {

	EntityType<ComplexEntity> T = EntityTypes.T(ComplexEntity.class);

	String getStringProperty();
	void setStringProperty(String stringProperty);

	LocalizedString getLocalizedStringProperty();
	void setLocalizedStringProperty(LocalizedString localizedStringProperty);

	Boolean getBooleanProperty();
	void setBooleanProperty(Boolean booleanProperty);

	Integer getIntegerProperty();
	void setIntegerProperty(Integer integerProperty);

	Double getDoubleProperty();
	void setDoubleProperty(Double doubleProperty);

	Object getObjectProperty();
	void setObjectProperty(Object objectProperty);

	SimpleEntity getSimpleEntityProperty();
	void setSimpleEntityProperty(SimpleEntity simpleEntityProperty);

	ComplexEntity getComplexEntityProperty();
	void setComplexEntityProperty(ComplexEntity complexEntityProperty);

	AnotherComplexEntity getAnotherComplexEntityProperty();
	void setAnotherComplexEntityProperty(AnotherComplexEntity anotherComplexEntityProperty);

	StandardIdentifiable getStandardIdentifiableProperty();
	void setStandardIdentifiableProperty(StandardIdentifiable StandardIdentifiableProperty);

	List<ComplexEntity> getComplexEntityList();
	void setComplexEntityList(List<ComplexEntity> complexEntityList);

	Map<String, ComplexEntity> getComplexEntityMap();
	void setComplexEntityMap(Map<String, ComplexEntity> complexEntityMap);

	List<String> getStringList();
	void setStringList(List<String> stringList);

	SimpleEnum getSimpleEnum();
	void setSimpleEnum(SimpleEnum simpleEnum);

	Object getBaseTypeProperty();
	void setBaseTypeProperty(Object baseTypeProperty);

}
