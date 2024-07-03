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
package com.braintribe.model.processing.workbench.action.api;

import java.util.List;

import com.braintribe.model.folder.Folder;
import com.braintribe.model.generic.path.ModelPath;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.workbench.WorkbenchAction;

public interface WorkbenchActionContext<A extends WorkbenchAction> {
	
	public GmSession getGmSession();
	public List<ModelPath> getModelPaths();
	public A getWorkbenchAction();
	public Object getPanel();
	public Folder getFolder();
	
	default ModelPath getRootModelPath() {
		return null;
	}
	
	default boolean isHandleInNewTab() {
		return true;
	}
	
	/**
	 * Used for instances which can set the workbenchAction after it has been created.
	 * @param workbenchAction - the action
	 */
	default void setWorkbenchAction(A workbenchAction) {
		//NOP
	}
	
	default boolean isUseForm() {
		return true;
	}
	
	/**
	 * Configures whether the form should be displayed.
	 * @param useForm - the configuration
	 */
	default void setUseForm(boolean useForm) {
		//NOP
	}

}
