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
package com.braintribe.model.access.impl;

import java.util.List;

import com.braintribe.model.access.AbstractDelegatingAccess;
import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.generic.manipulation.Manipulation;

public class ManipulationCollectingAccess extends AbstractDelegatingAccess {
	
	private List<Manipulation> manipulations;
	
	public ManipulationCollectingAccess(IncrementalAccess delegate, List<Manipulation> manipulations) {
		this.manipulations = manipulations;
		setDelegate(delegate);
	}
	
	@Override
	public ManipulationResponse applyManipulation(ManipulationRequest request)	throws ModelAccessException {
		
		ManipulationResponse response = super.applyManipulation(request);

		Manipulation requestManipulation = request.getManipulation(); 
		if (requestManipulation != null) {
			this.manipulations.add(requestManipulation);
		}
		
		Manipulation inducedManipulation = response.getInducedManipulation();
		if (inducedManipulation != null) {
			this.manipulations.add(inducedManipulation);	
		}
		
		return response;
	}
	
}
