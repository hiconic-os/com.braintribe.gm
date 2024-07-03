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
package com.braintribe.model.manipulation.parser.impl.model.ambig;

import java.math.BigDecimal;
import java.util.Date;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * To test handling of types with same simple name, this conflicts with {@link com.braintribe.model.manipulation.parser.impl.model.Joat}.
 */
public interface Joat extends GenericEntity {

	final EntityType<Joat> T = EntityTypes.T(Joat.class);

	String getStringValue();
	void setStringValue(String stringValue);

	boolean getBooleanValue();
	void setBooleanValue(boolean value);

	Date getDateValue();
	void setDateValue(Date value);

	BigDecimal getDecimalValue();
	void setDecimalValue(BigDecimal value);

	int getIntegerValue();
	void setIntegerValue(int value);

	long getLongValue();
	void setLongValue(long value);

	float getFloatValue();
	void setFloatValue(float value);

	double getDoubleValue();
	void setDoubleValue(double value);

}
