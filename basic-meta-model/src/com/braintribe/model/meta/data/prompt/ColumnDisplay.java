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
package com.braintribe.model.meta.data.prompt;

import java.util.List;

import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.EntityTypeMetaData;

/**
 * This metadata is used for configuring which columns to show in the clients' views.
 *
 */
public interface ColumnDisplay extends EntityTypeMetaData {
	
	EntityType<ColumnDisplay> T = EntityTypes.T(ColumnDisplay.class);
	
	void setDisplayNode(boolean displayNode);
	boolean getDisplayNode();
	
	void setNodeWidth(Integer nodeWidth);
	Integer getNodeWidth();
	
	void setNodeTitle(LocalizedString title);
	LocalizedString getNodeTitle();
	
	void setDisplayPaths(List<ColumnInfo> displayPaths);
	List<ColumnInfo> getDisplayPaths();
	
	void setPreventSingleEntryExpand(boolean prevent);
	boolean getPreventSingleEntryExpand();
	
	void setDisableExpansion(boolean disable);
	boolean getDisableExpansion();

}
