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
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.workbench.WorkbenchInstructionContext;
import com.braintribe.model.processing.workbench.WorkbenchInstructionExpert;
import com.braintribe.model.processing.workbench.WorkbenchInstructionProcessorException;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.workbench.WorkbenchPerspective;
import com.braintribe.model.workbench.instruction.AddFolderToPerspective;

/**
 * Adds a given folder to the perspective identified by given perspectiveName. 
 */
public class AddFolderToPerspectiveExpert implements WorkbenchInstructionExpert<AddFolderToPerspective> {

	@Override
	public void process(AddFolderToPerspective instruction, WorkbenchInstructionContext context) throws WorkbenchInstructionProcessorException {
	
		// Search for the target folder based on given path.
		WorkbenchPerspective perspective = getPerspective(context.getSession(), instruction.getPerspectiveName());
		
		if (shouldAddFolder(perspective, instruction)) {
			
			Folder folderToAdd = null;
			if (instruction.getUseExistingFolder()) {
				folderToAdd = context.findFolderByPath(instruction.getFolderToAdd().getName());
			}

			if (folderToAdd == null) {
				// We synchronize the new folder to ensure it's session based and 
				// assume a single folder returned by the synchronization. 
				folderToAdd = 
						BasicEntitySynchronization
						.newInstance(false)
						.session(context.getSession())
						.addEntity(instruction.getFolderToAdd())
						.addIdentityManager(new ResourceIdentityManager())
						.addDefaultIdentityManagers()
						.synchronize()
						.unique();
			}
			
			// Finally adding the synchronized folder to the sub folders of target folder.
			perspective.getFolders().add(folderToAdd);
		}
		
		
	}

	private WorkbenchPerspective getPerspective(PersistenceGmSession session, String perspectiveName) {
		EntityQuery query = EntityQueryBuilder.from(WorkbenchPerspective.T).where().property("name").eq(perspectiveName).done();
		WorkbenchPerspective perspective = session.queryCache().entities(query).first();
		if (perspective == null) {
			perspective = session.query().entities(query).first();
		}
		return perspective;
	}

	/**
	 * If the target folder already contains a folder with same name of folderToAdd and
	 * we shouldn't override existing folders we can skip this instruction. 
	 */
	private boolean shouldAddFolder(WorkbenchPerspective perspective, AddFolderToPerspective instruction) {
		if (perspective == null) return false;
		if (instruction.getOverrideExisting()) return true; // Add folder anyways.

		String folderToAddName = instruction.getFolderToAdd().getName();
		for (Folder subFolder : perspective.getFolders()) {
			if (subFolder.getName().equals(folderToAddName)) {
				return false;
			}
		}
		// No existing subFolder found. Add folder
		return true;
	}

}
