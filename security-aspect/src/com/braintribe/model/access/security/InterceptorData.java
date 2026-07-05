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
package com.braintribe.model.access.security;

import java.util.Set;

import com.braintribe.model.access.security.cloning.IlsExpertsRegistry;
import com.braintribe.model.access.security.manipulation.ApplyManipulationInterceptor;
import com.braintribe.model.access.security.query.QueryInterceptor;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpert;

/**
 * Wrapper for all data needed to configure a {@link QueryInterceptor} or {@link ApplyManipulationInterceptor} and some
 * additional functionality.
 */
public class InterceptorData {

	public IlsExpertsRegistry ilsExpertRegistry;
	public Set<? extends ManipulationSecurityExpert> manipulationSecurityExperts;
	public Set<String> trustedRoles;

	public InterceptorData(IlsExpertsRegistry ilsExpertRegistry, Set<? extends ManipulationSecurityExpert> manipulationSecurityExperts,
			Set<String> trustedRoles) {

		this.ilsExpertRegistry = ilsExpertRegistry;
		this.manipulationSecurityExperts = manipulationSecurityExperts;
		this.trustedRoles = trustedRoles;
	}

}
