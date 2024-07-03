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
package com.braintribe.model.generic.reflection;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link TraversingVisitor} implementation that passes the {@link TraversingContext} to its
 * {@link #setDelegates(List) delegates}.
 * 
 * @author michael.lafite
 */
public class CompoundTraversingVisitor implements TraversingVisitor {

	private List<TraversingVisitor> delegates;

	public CompoundTraversingVisitor() {
		// nothing to do;
	}

	public CompoundTraversingVisitor(List<TraversingVisitor> delegates) {
		this.delegates = delegates;
	}
	
	public CompoundTraversingVisitor(TraversingVisitor... delegates) {
		this.delegates = Arrays.asList(delegates);
	}

	public List<TraversingVisitor> getDelegates() {
		return this.delegates;
	}

	public void setDelegates(List<TraversingVisitor> delegates) {
		this.delegates = delegates;
	}

	@Override
	public void visitTraversing(TraversingContext traversingContext) {
		if (this.delegates != null) {
			for (TraversingVisitor delegate : this.delegates) {
				delegate.visitTraversing(traversingContext);
			}
		}
	}
}
