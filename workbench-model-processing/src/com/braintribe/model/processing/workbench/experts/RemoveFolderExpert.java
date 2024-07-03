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

import java.util.Iterator;

import com.braintribe.model.folder.Folder;
import com.braintribe.model.processing.workbench.WorkbenchInstructionContext;
import com.braintribe.model.processing.workbench.WorkbenchInstructionExpert;
import com.braintribe.model.processing.workbench.WorkbenchInstructionProcessorException;
import com.braintribe.model.workbench.instruction.RemoveFolder;


/**
 * Identifies a folder by specified path and removes a subFolder of this folder identified by
 * name specified in folderToRemove. 
 */
public class RemoveFolderExpert implements WorkbenchInstructionExpert<RemoveFolder> {
	
	@Override
	public void process(RemoveFolder instruction, WorkbenchInstructionContext context) throws WorkbenchInstructionProcessorException {
		
		// Search for the target folder based on given path.
		Folder folder = context.getFolderByPath(instruction.getPath());
		
		// Iterate through subFolders and remove subFolders with specified name.
		Iterator<Folder> subFolderIterator = folder.getSubFolders().iterator();
		while (subFolderIterator.hasNext()) {
			Folder subFolder = subFolderIterator.next();
			if (subFolder.getName().equals(instruction.getFolderToRemove())) {
				subFolderIterator.remove();
			}
		}
		
	}

}
