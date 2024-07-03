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
 * A <code>Font</code> entity represents a Font style, name, size, weight,..
 * 
 */
public interface Font extends GenericEntity {

	EntityType<Font> T = EntityTypes.T(Font.class);

	Color getColor();
	void setColor(Color color);

	String getFamily();
	void setFamily(String family);

	ValueWithUnit getSize();
	void setSize(ValueWithUnit size);

	FontStyle getStyle();
	void setStyle(FontStyle style);

	FontStretch getStrech();
	void setStrech(FontStretch strech);

	FontWeight getWeight();
	void setWeight(FontWeight weight);

	FontVariant getVariant();
	void setVariant(FontVariant variant);

	String getFontUrl();
	void setFontUrl(String fonUrl);

}
