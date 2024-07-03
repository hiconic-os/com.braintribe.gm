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
package com.braintribe.model.meta.data.display;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.UniversalMetaData;

/**
 * Indicates that given model element's name should be converted and how.
 * <p>
 * This is useful to specify a name conversion for a wide range of elements with very few MD instances.
 * 
 * @author peter.gazdik
 */
public interface NameConversion extends UniversalMetaData {

	EntityType<NameConversion> T = EntityTypes.T(NameConversion.class);

	// NOTE these might not exist, so if needed, create it 
	String NAME_CONVERSION_SNAKE_CASE_GLOBAL_ID = "nameConversion:snakeCase";
	String NAME_CONVERSION_SCREAMING_SNAKE_CASE_GLOBAL_ID = "nameConversion:screamingSnakeCase";
	
	NameConversionStyle getStyle();
	void setStyle(NameConversionStyle style);

}
