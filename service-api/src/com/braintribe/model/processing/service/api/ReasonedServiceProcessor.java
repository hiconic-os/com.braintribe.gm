// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.service.api;

import static com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling.getOrTunnel;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * ReasonedServiceProcessor is  trait like interface that adapts the {@link Maybe} returning
 * {@link #processReasoned(ServiceRequestContext, ServiceRequest)} to the {@link ServiceProcessor#process(ServiceRequestContext, ServiceRequest)}
 * method.
 * 
 * @author Dirk Scheffler
 */
public interface ReasonedServiceProcessor<P extends ServiceRequest, R> extends ServiceProcessor<P, R>{
	@Override
	default R process(ServiceRequestContext requestContext, P request) {
		return getOrTunnel(processReasoned(requestContext, request));
	}
	
	Maybe<? extends R> processReasoned(ServiceRequestContext context, P request);
}
