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
package com.braintribe.model.workbench.instruction;

import com.braintribe.model.folder.Folder;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;


public interface AddFolderToPerspective extends WorkbenchInstruction {
	
	EntityType<AddFolderToPerspective> T = EntityTypes.T(AddFolderToPerspective.class);
	
	void setPerspectiveName (String perspectiveName);
	String getPerspectiveName();
	
	void setFolderToAdd(Folder folderToAdd);
	Folder getFolderToAdd();

	@Initializer("false")
	boolean getOverrideExisting();
	void setOverrideExisting(boolean overrideExisting);
	
	void setUseExistingFolder(boolean useExistingFolder);
	boolean getUseExistingFolder();
	
	

}
