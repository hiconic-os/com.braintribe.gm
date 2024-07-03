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

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Description;

/**
 * Base type for the {@link TemplateServiceRequestBasedAction} and the {@link TemplateQueryAction}.
 * @author michel.docouto
 */
@Abstract
public interface TemplateServiceRequestBasedAction extends TemplateBasedAction {

	@Description("Specifies whether auto paging is automatically configured for the template actions. The ServiceRequest should be a HasPagination in case of TemplateQueryAction.")
	public boolean getAutoPaging();
	public void setAutoPaging(boolean autoPaging);

	/**
	 * Configures the execution type. If null is set, then we should use the OLD UI.
	 * If something is set, then we use the NEW UI.
	 */
	@Description("Specifies the type of the execution. If something is set, then the Normalized UI is displayed. If auto or autoEditable are set, then the request is automatically executed.")
	public ExecutionType getExecutionType();
	public void setExecutionType(ExecutionType executionType);

}
