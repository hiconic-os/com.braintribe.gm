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

@Description("This interface contains all necessary information to post a Callback Request to a specified REST endpoint (e.g., Web Server).")
public interface AsynchronousRequestRestCallback extends AsynchronousRequestCallback {

	final EntityType<AsynchronousRequestRestCallback> T = EntityTypes.T(AsynchronousRequestRestCallback.class);
	
	String url = "url";
	String statusUrl = "statusUrl";
	String basicAuthenticationUser = "basicAuthenticationUser";
	String basicAuthenticationPassword = "basicAuthenticationPassword";
	String customData = "customData";
	String endpointServiceId = "endpointServiceId";
	String callbackProcessorServiceDomain = "callbackProcessorServiceDomain";
	
	void setUrl(String url);
	@Name("Callback URL")
	@Description("The URL that should be used for sending a POST request containing the Callback Request after the item of work has been completed.")
	String getUrl();

	void setStatusUrl(String statusUrl);
	@Name("Callback Status URL")
	@Description("The URL that for sending intermediate status update Callback Requests. These update POST requests can happen multiple time until a final completion Callback Request is issued. There is no guarantee for status Callbacks.")
	String getStatusUrl();

	void setBasicAuthenticationUser(String basicAuthenticationUser);
	@Name("Callback Username (Basic Authentication)")
	@Description("The username, if the provided endpoint requires Basic HTTP authentication.")
	String getBasicAuthenticationUser();

	void setBasicAuthenticationPassword(String basicAuthenticationPassword);
	@Name("Callback Password (Basic Authentication) (Encrypted)")
	@Description("The password, if the provided endpoint requires Basic HTTP authentication. Note that the password must not be in plaintext but rather has to be encrypted (see documentation for more details).")
	String getBasicAuthenticationPassword();

	void setCustomData(String customData);
	@Name("Callback Custom Data")
	@Description("Any kind of data that will be forwarded to the receiving Callback Processor. This should help the Callback Processor to identify the item of work that has been finished.")
	String getCustomData();

	void setEndpointServiceId(String endpointServiceId);
	@Name("Callback Endpoint Service Id")
	@Description("If the receiving endpoint is a tribefire installation, this holds the Id of the service processor that should handle the Callback Request. This information is optional.")
	String getEndpointServiceId();
	
	void setCallbackProcessorServiceDomain(String callbackProcessorServiceDomain);
	@Name("Callback Processor Service Domain")
	@Description("If the receiving endpoint is a tribefire installation, this holds the Id of the service domain (e.g., Access) that provides the Service Processor for the Callback Request. This information is optional.")
	String getCallbackProcessorServiceDomain();
}
