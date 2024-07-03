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
package com.braintribe.model.service.api.callback;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Description("Holds all the information necessary to invoke a Callback Request at the specified processor.")
public interface AsynchronousRequestProcessorCallback extends AsynchronousRequestCallback {

	final EntityType<AsynchronousRequestProcessorCallback> T = EntityTypes.T(AsynchronousRequestProcessorCallback.class);
	
	String callbackProcessorId = "callbackProcessorId";
	String callbackProcessorCustomData = "callbackProcessorCustomData";
	String callbackProcessorServiceDomain = "callbackProcessorServiceDomain";

	//External ID of a service processor that should handle the callback request
	//This processor must be able to process <? super ConversionCallbackRequest> 
	void setCallbackProcessorId(String callbackProcessorId);
	@Name("Callback Processor Id")
	@Description("The Id of the service processor that should handle the request. When this is not set, the Callback Service Domain has to be set instead.")
	String getCallbackProcessorId();

	//Might be any kind of data, e.g., a URL
	void setCallbackProcessorCustomData(String callbackProcessorCustomData);
	@Name("Callback Custom Data")
	@Description("Any kind of data that will be forwarded to the receiving Callback Processor. This should help the Callback Processor to identify the item of work that has been finished.")
	String getCallbackProcessorCustomData();

	void setCallbackProcessorServiceDomain(String callbackProcessorServiceDomain);
	@Name("Callback Processor Service Domain")
	@Description("The Service Domain (e.g., an Access) where the Callback Processor is registered. This information is necessary when the Callback Processor Id is not available / set.")
	String getCallbackProcessorServiceDomain();

}
