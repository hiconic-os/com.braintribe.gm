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
package com.braintribe.marshaller.impl.basic.test.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface JackOfAllTrades extends StandardIdentifiable {

	EntityType<JackOfAllTrades> T = EntityTypes.T(JackOfAllTrades.class);

	List<Object> getObjectList();
	void setObjectList(List<Object> objectList);

	Object getObject();
	void setObject(Object object);

	// test Object property with empty List value
	Object getObjectL();
	void setObjectL(Object objectL);
	
	// test Object property with empty Set value
	Object getObjectS();
	void setObjectS(Object objectS);
	
	// test Object property with empty Map value
	Object getObjectM();
	void setObjectM(Object objectM);
	
	Date getDateValue();
	void setDateValue(Date dateValue);

	Mode getMode();
	void setMode(Mode mode);

	JackOfAllTrades getOther();
	void setOther(JackOfAllTrades otherExample);

	String getStringValue();
	void setStringValue(String stringValue);

	Boolean getBooleanValue();
	void setBooleanValue(Boolean booleanValue);

	Integer getIntegerValue();
	void setIntegerValue(Integer integerValue);

	Long getLongValue();
	void setLongValue(Long longValue);

	Float getFloatValue();
	void setFloatValue(Float floatValue);

	Double getDoubleValue();
	void setDoubleValue(Double doubleValue);

	boolean getPrimitiveBooleanValue();
	void setPrimitiveBooleanValue(boolean primitiveBooleanValue);

	int getPrimitiveIntegerValue();
	void setPrimitiveIntegerValue(int primitiveIntegerValue);

	long getPrimitiveLongValue();
	void setPrimitiveLongValue(long primitiveLongValue);

	float getPrimitiveFloatValue();
	void setPrimitiveFloatValue(float primitiveFloatValue);

	double getPrimitiveDoubleValue();
	void setPrimitiveDoubleValue(double primitiveDoubleValue);

	BigDecimal getDecimalValue();
	void setDecimalValue(BigDecimal decimalValue);

	List<String> getStringList();
	void setStringList(List<String> stringList);

	List<JackOfAllTrades> getEntityList();
	void setEntityList(List<JackOfAllTrades> entityList);

	Set<String> getStringSet();
	void setStringSet(Set<String> stringSet);

	Set<JackOfAllTrades> getEntitySet();
	void setEntitySet(Set<JackOfAllTrades> entitySet);

	Map<String, String> getStringStringMap();
	void setStringStringMap(Map<String, String> stringStringMap);

	Map<JackOfAllTrades, JackOfAllTrades> getEntityEntityMap();
	void setEntityEntityMap(Map<JackOfAllTrades, JackOfAllTrades> entityEntityMap);

}
