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
package com.braintribe.model.workbench;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;

public interface WorkbenchConfiguration extends GenericEntity {

	EntityType<WorkbenchConfiguration> T = EntityTypes.T(WorkbenchConfiguration.class);

	Resource getStylesheet();
	void setStylesheet(Resource stylesheet);
	
	Resource getFavIcon();
	void setFavIcon(Resource favIcon);
	
	String getTitle();
	void setTitle(String title);
	
	Integer getAutoPagingSize();
	void setAutoPagingSize(Integer autoPagingSize);
	
	boolean getUseGlobalSearch();
	void setUseGlobalSearch(boolean useGlobalSearch);
	
	boolean getUseTopContextActionBar();
	void setUseTopContextActionBar(boolean useTopContextActionBar);
	
	boolean getUseTopGlobalActionBar();
	void setUseTopGlobalActionBar(boolean useTopGlobalActionBar);
	
	String getLocale();
	void setLocale(String locale);
	
	Integer getMaxNumberOfOpenedTabs();
	void setMaxNumberOfOpenedTabs(Integer maxNumberOfOpenedTabs);
	
	Integer getWorkbenchWidth();
	void setWorkbenchWidth(Integer workbenchWidth);
	
	boolean getDisplayShortName();
	void setDisplayShortName(boolean displayShortName);
}
