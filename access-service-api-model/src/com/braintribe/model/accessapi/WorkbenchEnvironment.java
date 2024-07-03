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
package com.braintribe.model.accessapi;

import com.braintribe.model.generic.annotation.Abstract;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import java.util.List;
import java.util.Set;

import com.braintribe.model.folder.Folder;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.workbench.WorkbenchConfiguration;
import com.braintribe.model.workbench.WorkbenchPerspective;

@Abstract
public interface WorkbenchEnvironment extends GenericEntity {

	final EntityType<WorkbenchEnvironment> T = EntityTypes.T(WorkbenchEnvironment.class);

	// @formatter:off
	void setWorkbenchRootFolders(Set<Folder> rootFolders);
	Set<Folder> getWorkbenchRootFolders();
	
	List<WorkbenchPerspective> getPerspectives();
	void setPerspectives(List<WorkbenchPerspective> perspectives);
	
	void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration);
	WorkbenchConfiguration getWorkbenchConfiguration();
	// @formatter:on

}
