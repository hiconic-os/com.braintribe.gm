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
package com.braintribe.model.processing.session.api.persistence;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.session.exception.GmSessionException;

public class CommitCapture {

	private static class ManipulationCollectingListener implements CommitListener {
		
		private final List<Manipulation> manipulations;
		
		public ManipulationCollectingListener(List<Manipulation> manipulations) {
			this.manipulations = manipulations;
		}
	
		@Override
		public void onBeforeCommit(PersistenceGmSession session, Manipulation manipulation) {
			// noop
		}
	
		@Override
		public void onAfterCommit(PersistenceGmSession session,	Manipulation manipulation, Manipulation inducedManipluation) {
			if (manipulation != null) {
				this.manipulations.add(manipulation);
			}
			if (inducedManipluation != null) {
				this.manipulations.add(inducedManipluation);	
			}
		}
	}

	public static Manipulation commitAndCaptureManipulations( PersistenceGmSession session) throws GmSessionException {
		List<Manipulation> collectedManipulations = new ArrayList<Manipulation>();			
		ManipulationCollectingListener commitListener = new ManipulationCollectingListener(collectedManipulations);
		session.listeners().add(commitListener);
		
		session.commit();

		switch (collectedManipulations.size()) {
			case 0:
				return null;
			case 1: 
				return collectedManipulations.get(0);
			default:
				return CompoundManipulation.create(collectedManipulations);
		}				
	}
}
