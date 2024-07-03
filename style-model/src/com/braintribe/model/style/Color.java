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
package com.braintribe.model.style;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A <code>Color</code> entity represents a color defined by three values (i.e. red, green, blue) where each value is an
 * integer from <code>0</code> to <code>255</code>.
 * 
 * @author michael.lafite
 */

public interface Color extends GenericEntity {

	EntityType<Color> T = EntityTypes.T(Color.class);

	Integer getRed();
	void setRed(Integer red);

	Integer getGreen();
	void setGreen(Integer green);

	Integer getBlue();
	void setBlue(Integer blue);
	
	default Color color(String cssColorCode) {
		int index = 0;
		if (cssColorCode.startsWith("#"))
			index = 1;
		
		setRed(Integer.parseInt(cssColorCode.substring(index, index + 2), 16));
		setGreen(Integer.parseInt(cssColorCode.substring(index + 2, index + 4), 16));
		setBlue(Integer.parseInt(cssColorCode.substring(index + 4, index + 6), 16));
		
		return this;
	}
	
	default Color initColor(int r, int g, int b) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		
		return this;
	}
	
	static Color create(String cssColorCode) {
		return T.create().color(cssColorCode);
	}
	
	static Color create(int r, int g, int b) {
		return T.create().initColor(r, g, b);
	}
	
}
