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
package com.braintribe.model.example;

import java.util.Date;
import java.util.Set;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@SelectiveInformation("${longProperty} ${stringProperty}")

public interface Omega extends StandardIdentifiable {

	EntityType<Omega> T = EntityTypes.T(Omega.class);

	String longProperty = "longProperty";
	String stringProperty = "stringProperty";
	String integerProperty = "integerProperty";
	String doubleProperty = "doubleProperty";
	String floatProperty = "floatProperty";
	String dateProperty = "dateProperty";
	String booleanProperty = "booleanProperty";
	String enumProperty = "enumProperty";
	String entityProperty = "entityProperty";

	String setOfLongProperty = "setOfLongProperty";
	String setOfStringProperty = "setOfStringProperty";
	String setOfIntegerProperty = "setOfIntegerProperty";
	String setOfDoubleProperty = "setOfDoubleProperty";
	String setOfFloatProperty = "setOfFloatProperty";
	String setOfDateProperty = "setOfDateProperty";
	String setOfBooleanProperty = "setOfBooleanProperty";
	String setOfEnumProperty = "setOfEnumProperty";
	String setOfEntityProperty = "setOfEntityProperty";

	Long getLongProperty();
	void setLongProperty(Long longProperty);

	Set<Long> getSetOfLongProperty();
	void setSetOfLongProperty(Set<Long> setOfLongProperty);

	String getStringProperty();
	void setStringProperty(String stringProperty);

	Set<String> getSetOfStringProperty();
	void setSetOfStringProperty(Set<String> setOfStringProperty);

	Integer getIntegerProperty();
	void setIntegerProperty(Integer integerProperty);

	Set<Integer> getSetOfIntegerProperty();
	void setSetOfIntegerProperty(Set<Integer> setOfIntegerProperty);

	Double getDoubleProperty();
	void setDoubleProperty(Double doubleProperty);

	Set<Double> getSetOfDoubleProperty();
	void setSetOfDoubleProperty(Set<Double> setOfDoubleProperty);

	Float getFloatProperty();
	void setFloatProperty(Float floatProperty);

	Set<Float> getSetOfFloatProperty();
	void setSetOfFloatProperty(Set<Float> setOfFloatProperty);

	Date getDateProperty();
	void setDateProperty(Date dateProperty);

	Set<Date> getSetOfDateProperty();
	void setSetOfDateProperty(Set<Date> setOfDateProperty);

	Boolean getBooleanProperty();
	void setBooleanProperty(Boolean booleanProperty);

	Set<Boolean> getSetOfBooleanProperty();
	void setSetOfBooleanProperty(Set<Boolean> setOfBooleanProperty);

	OmegaEnum getEnumProperty();
	void setEnumProperty(OmegaEnum enumProperty);

	Set<OmegaEnum> getSetOfEnumProperty();
	void setSetOfEnumProperty(Set<OmegaEnum> setOfEnumProperty);

	Alpha getEntityProperty();
	void setEntityProperty(Alpha entityProperty);

	Set<Alpha> getSetOfEntityProperty();
	void setSetOfEntityProperty(Set<Alpha> setOfEntityProperty);

}
