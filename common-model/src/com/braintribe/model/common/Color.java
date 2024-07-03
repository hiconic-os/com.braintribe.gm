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
package com.braintribe.model.common;


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A <code>Color</code> entity represents a color defined by three values (i.e. red, green, blue) where each value is an
 * integer from <code>0</code> to <code>255</code>.
 * 
 * @author michael.lafite
 */

public interface Color extends IdentifiableEntity {

	EntityType<Color> T = EntityTypes.T(Color.class);

	Integer getRed();
	void setRed(Integer red);

	Integer getGreen();
	void setGreen(Integer green);

	Integer getBlue();
	void setBlue(Integer blue);

}
