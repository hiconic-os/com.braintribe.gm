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
package com.braintribe.model.generic.tracking;

import java.util.Stack;

import com.braintribe.model.generic.manipulation.Manipulation;

public abstract class AbstractStackingManipulationTracker implements ManipulationTracker {
	
	protected abstract Stack<ManipulationCollector> getCollectorStack();
	
	@Override
	public ManipulationCollector getCurrentManipulationCollector() {
		Stack<ManipulationCollector> contextStack = getCollectorStack();
		return !contextStack.isEmpty()? contextStack.peek(): null;
	}
	
	@Override
	public ManipulationCollector begin() {
		ManipulationCollector manipulationCollector = new StandardManipulationCollector();
		getCollectorStack().push(manipulationCollector);
		return manipulationCollector;
	}
	
	@Override
	public ManipulationCollector stop() throws ManipulationTrackingException {
		try {
			return getCollectorStack().pop();
		} catch (Exception e) {
			throw new ManipulationTrackingException("error while stopping tracking", e);
		}
	}
	
	@Override
	public void begin(ManipulationCollector manipulationContext)
			throws ManipulationTrackingException {
		getCollectorStack().push(manipulationContext);
	}
	
	@Override
	public void noticeManipulation(Manipulation manipulation) {
		ManipulationCollector manipulationContext = getCurrentManipulationCollector();
		if (manipulationContext != null)
			manipulationContext.noticeManipulation(manipulation);
	}
}
