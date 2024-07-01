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
package com.braintribe.model.processing.traversing.engine.impl;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.processing.traversing.api.GmTraversingException;
import com.braintribe.model.processing.traversing.api.GmTraversingVisitor;
import com.braintribe.model.processing.traversing.engine.api.TraversingConfigurer;
import com.braintribe.model.processing.traversing.engine.api.customize.ModelWalkerCustomization;
import com.braintribe.model.processing.traversing.engine.impl.walk.ModelWalker;

public abstract class AbstractTraversingConfigurer<T extends AbstractTraversingConfigurer<T>> implements TraversingConfigurer<T> {

	private final List<GmTraversingVisitor> visitors = new ArrayList<GmTraversingVisitor>();
	private final T self;
	private final ModelWalker modelWalker = new ModelWalker();

	public AbstractTraversingConfigurer() {
		this.self = (T) this;
		visitors.add(modelWalker);
	}

	@Override
	public T depthFirstWalk() {
		modelWalker.setBreadthFirst(false);
		return self;
	}

	@Override
	public T breadthFirstWalk() {
		modelWalker.setBreadthFirst(true);
		return self;
	}

	@Override
	public T customWalk(GmTraversingVisitor walker) {
		visitors.remove(modelWalker);
		return visitor(walker);
	}

	@Override
	public T visitor(GmTraversingVisitor visitor) {
		visitors.add(visitor);
		return self;
	}

	@Override
	public void doFor(Object target) throws GmTraversingException {
		GmTraversingVisitor[] visitorArray = visitors.toArray(new GmTraversingVisitor[visitors.size()]);
		TraversingWorker worker = new TraversingWorker(visitorArray, target);
		worker.run();
	}

	@Override
	public T customizeDefaultWalker(ModelWalkerCustomization customization) {
		modelWalker.setWalkerCustomization(customization);
		return self;
	}

}
