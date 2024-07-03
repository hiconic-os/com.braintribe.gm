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
package com.braintribe.model.processing.query.autocompletion.impl.lexer.container;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmType;

public interface AliasType extends StandardIdentifiable {
	
	final EntityType<AliasType> T = EntityTypes.T(AliasType.class);

	public static enum CheckType {
		None, Alias, Property
	}

	GmType getEntityType();

	void setEntityType(GmType value);

	String getLastAliasPart();

	void setLastAliasPart(String value);

	CheckType getCheckType();

	void setCheckType(CheckType value);

	boolean getHasProperties();

	void setHasProperties(boolean value);

	boolean getPropertyFound();

	void setPropertyFound(boolean value);
	
	GmType getPropertyType();
	
	void setPropertyType(GmType propertyType);
}
