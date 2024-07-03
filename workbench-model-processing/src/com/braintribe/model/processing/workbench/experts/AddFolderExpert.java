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
package com.braintribe.model.processing.workbench.experts;

import com.braintribe.model.folder.Folder;
import com.braintribe.model.processing.generic.synchronize.BasicEntitySynchronization;
import com.braintribe.model.processing.generic.synchronize.experts.ResourceIdentityManager;
import com.braintribe.model.processing.workbench.WorkbenchInstructionContext;
import com.braintribe.model.processing.workbench.WorkbenchInstructionExpert;
import com.braintribe.model.processing.workbench.WorkbenchInstructionProcessorException;
import com.braintribe.model.workbench.instruction.AddFolder;

/**
 * Adds a given folder to the subFolder list of target folder identified by given path. 
 */
public class AddFolderExpert implements WorkbenchInstructionExpert<AddFolder> {

	@Override
	public void process(AddFolder instruction, WorkbenchInstructionContext context) throws WorkbenchInstructionProcessorException {
	
		// Search for the target folder based on given path.
		Folder folder = context.getFolderByPath(instruction.getPath());
		
		if (shouldAddFolder(folder, instruction)) {
			
			// We synchronize the new folder to ensure it's session based and 
			// assume a single folder returned by the synchronization. 
			Folder synchronizedFolderToAdd = 
					BasicEntitySynchronization
					.newInstance(false)
					.session(context.getSession())
					.addEntity(instruction.getFolderToAdd())
					.addIdentityManager(new ResourceIdentityManager())
					.addDefaultIdentityManagers()
					.synchronize()
					.unique();
			
			// Finally adding the synchronized folder to the sub folders of target folder.
			folder.getSubFolders().add(synchronizedFolderToAdd);
		}
		
		
	}

	/**
	 * If the target folder already contains a folder with same name of folderToAdd and
	 * we shouldn't override existing folders we can skip this instruction. 
	 */
	private boolean shouldAddFolder(Folder folder, AddFolder instruction) {
		if (instruction.getOverrideExisting()) return true; // Add folder anyways.

		String folderToAddName = instruction.getFolderToAdd().getName();
		for (Folder subFolder : folder.getSubFolders()) {
			if (subFolder.getName().equals(folderToAddName)) {
				return false;
			}
		}
		// No existing subFolder found. Add folder
		return true;
	}

}
