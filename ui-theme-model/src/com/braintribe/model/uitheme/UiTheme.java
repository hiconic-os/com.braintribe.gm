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
package com.braintribe.model.uitheme;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.style.Color;
import com.braintribe.model.style.Font;

/**
 * 
 *         UiTheme Template with basic variables to configure Colors and Fonts in Tribefire menus, grids, dialogs,..
 */
public interface UiTheme extends GenericEntity {

	EntityType<UiTheme> T = EntityTypes.T(UiTheme.class);

	Color getHooverColor();
	void setHooverColor(Color hooverColor);

	Color getSelectColor();
	void setSelectColor(Color selectInactiveColor);

	Color getSelectInactiveColor();
	void setSelectInactiveColor(Color selectInactiveColor);

	Font getCaptionFont();
	void setCaptionFont(Font captionFont);

	Font getTetherFont();
	void setTetherFont(Font tetherFont);

	Font getTabFont();
	void setTabFont(Font tabFont);

	Font getBasicFont();
	void setBasicFont(Font basicFont);

	Font getMenuFont();
	void setMenuFont(Font menuFont);

	Font getHeaderFont();
	void setHeaderFont(Font headerFont);

}
