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
package com.braintribe.model.service.api;

import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * ServiceRequest is the base type from which all service request types must derive in order to be processed by an {@link Evaluator}.
 * @author Dirk Scheffler
 *
 */
@Abstract
public interface ServiceRequest extends GenericEntity {

	EntityType<ServiceRequest> T = EntityTypes.T(ServiceRequest.class);

	void setMetaData(Map<String, Object> metaData);
	Map<String, Object> getMetaData();

	EvalContext<?> eval(Evaluator<ServiceRequest> evaluator);

	/**
	 * @return true if a request can be intercepted by AOP interceptors
	 */
	default boolean interceptable() {
		return true;
	}

	/**
	 * @return true if a request can be dispatched as a {@link DispatchableRequest}
	 */
	default boolean dispatchable() {
		return false;
	}

	/**
	 * @return true if the request is a system request which means that it cannot be dynamically mapped to processors or interceptors and is only hardwired
	 */
	default boolean system() {
		return false;
	}

	/**
	 * @return true if the request is to be authorized as an {@link AuthorizedRequest}
	 */
	default boolean requiresAuthentication() {
		return false;
	}
	
	/**
	 * @return true if the request is to be authorized as an {@link AuthorizableRequest}
	 */
	default boolean supportsAuthentication() {
		return false;
	}
	
	/**
	 * @deprecated The feature which relies on this method will soon be no longer supported. This method will be removed.
	 */
	@Deprecated
	default boolean requiresResponseEncryption() {
		return false;
	}

	/**
	 * @return the id of the service domain that configures the model that maps processors
	 */
	default String domainId() {
		return null;
	}

	@Override
	default String stringify() {
		StringBuilder sb = new StringBuilder();
		sb.append(GenericEntity.super.stringify());
		Map<String, Object> metaData = getMetaData();
		if (metaData != null && !metaData.isEmpty()) {
			sb.append(", meta-data: [");
			boolean first = true;
			for (Map.Entry<String,Object> entry : metaData.entrySet()) {
				if (first) {
					first = false;
				} else {
					sb.append(',');
				}
				sb.append(entry.getKey());
				sb.append('=');
				sb.append(entry.getValue());
			}
			sb.append("]");
		}
		return sb.toString();
	}
}
