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

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * WorkbenchAction which can be configured with a {@link ServiceRequest} which will be executed.
 */
public interface ServiceRequestAction extends WorkbenchAction {

	EntityType<ServiceRequestAction> T = EntityTypes.T(ServiceRequestAction.class);
	
	@Description("Specifies the request which will be executed by the action.")
	public ServiceRequest getRequest();
	public void setRequest(ServiceRequest request);
	
	@Description("Specifies the type of the execution. It can be either auto (default), autoEditable or editable. Editable means showing the request properties for edition.")
	public ExecutionType getExecutionType();
	public void setExecutionType(ExecutionType executionType);
	
	@Description("Specifies whether auto paging is automatically configured for the ServiceRequest execution. The ServiceRequest should be a HasPagination.")
	public boolean getAutoPaging();
	public void setAutoPaging(boolean autoPaging);
	
	@Description("Specifies whether the auto paging configuration can be manually changed by users via the UI.")
	public boolean getPagingEditable();
	public void setPagingEditable(boolean pagingEditable);
}

