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
package com.braintribe.model.processing.query.test.stringifier.model;

import java.math.BigDecimal;

import com.braintribe.model.generic.StandardStringIdentifiable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.processing.query.test.model.Color;

public interface TypeTestModel extends StandardStringIdentifiable {

	EntityType<TypeTestModel> T = EntityTypes.T(TypeTestModel.class);

	Double getDoubleValue();
	void setDoubleValue(Double doubleValue);

	BigDecimal getDecimalValue();
	void setDecimalValue(BigDecimal decimalValue);

	Float getFloatValue();
	void setFloatValue(Float floatValue);

	Long getLongValue();
	void setLongValue(Long longValue);

	Integer getIntValue();
	void setIntValue(Integer intValue);

	Boolean getBoolValue();
	void setBoolValue(Boolean boolValue);

	Color getEnumValue();
	void setEnumValue(Color enumValue);

	TypeTestModel getEntityValue();
	void setEntityValue(TypeTestModel entityValue);

}
