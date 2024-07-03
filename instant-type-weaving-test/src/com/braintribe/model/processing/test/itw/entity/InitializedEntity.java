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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface InitializedEntity extends GenericEntity {

	EntityType<InitializedEntity> T = EntityTypes.T(InitializedEntity.class);

	@Initializer("'hello'")
	String getStringValue();
	void setStringValue(String value);

	@Initializer("99")
	int getIntValue();
	void setIntValue(int value);

	@Initializer("11L")
	long getLongValue();
	void setLongValue(long value);

	@Initializer("+123f")
	float getFloatValue();
	void setFloatValue(float value);

	@Initializer("-123D")
	double getDoubleValue();
	void setDoubleValue(double value);

	@Initializer("+1.0e30f")
	float getBigFloatValue();
	void setBigFloatValue(float value);

	@Initializer("-1.0e30d")
	double getBigDoubleValue();
	void setBigDoubleValue(double value);

	@Initializer("true")
	boolean getBooleanValue();
	void setBooleanValue(boolean value);

	@Initializer("99889988.00b")
	BigDecimal getDecimalValue();
	void setDecimalValue(BigDecimal value);

	@Initializer("now()")
	Date getDateValue();
	void setDateValue(Date value);

	@Initializer("enum(com.braintribe.model.processing.test.itw.entity.Color,green)")
	Color getEnumValue();
	void setEnumValue(Color value);

	@Initializer("green")
	Color getEnumShort();
	void setEnumShort(Color value);

	Date getUninitializedDateValue();
	void setUninitializedDateValue(Date value);

	@Initializer("['one','two']")
	List<String> getListValue();
	void setListValue(List<String> listValue);

	@Initializer("{'one','two'}")
	Set<String> getSetValue();
	void setSetValue(Set<String> setValue);

	@Initializer("{green}")
	Set<Color> getEnumSetValue();
	void setEnumSetValue(Set<Color> value);

	@Initializer("map[1,'one',2,'two']")
	Map<Integer, String> getMapValue();
	void setMapValue(Map<Integer, String> mapValue);

}
