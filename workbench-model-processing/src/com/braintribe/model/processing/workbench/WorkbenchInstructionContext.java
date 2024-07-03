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
package com.braintribe.model.processing.workbench;

import java.util.List;

import com.braintribe.model.folder.Folder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.workbench.instruction.WorkbenchInstruction;

public interface WorkbenchInstructionContext {
	
	/**
	 * Returns the {@link PersistenceGmSession}
	 */
	PersistenceGmSession getSession();
	
	/**
	 * Searches for a folder under given path. 
	 * Returns null if no folder could be found.
	 */
	Folder findFolderByPath(String path) throws WorkbenchInstructionProcessorException;

	/**
	 * Searches for a folder under given path.
	 * Throws {@link FolderNotFoundException} in case no folder could be found. 
	 */
	Folder getFolderByPath(String path) throws WorkbenchInstructionProcessorException, FolderNotFoundException;

	/**
	 * Returns the subFolder of the given parentFolder with specified folderName.
	 * This method returns null if no according subFolder could be found. 
	 */
	Folder findSubFolder(String folderName, Folder parentFolder) throws WorkbenchInstructionProcessorException;
	
	/**
	 * Returns a root folder identified by name. A root folder is defined by having no parent set.
	 * This method returns null if no according folder could be found.
	 */
	Folder findRootFolder(String folderName) throws WorkbenchInstructionProcessorException;
	
	/**
	 * Returns a list of elements of given path.
	 */
	List<String> getPathElements(String path);
	
	/**
	 * Returns the registered expert for the given instruction.
	 */
	WorkbenchInstructionExpert<WorkbenchInstruction> getExpertForInstruction(WorkbenchInstruction instruction) throws WorkbenchInstructionProcessorException;
	
	
}
