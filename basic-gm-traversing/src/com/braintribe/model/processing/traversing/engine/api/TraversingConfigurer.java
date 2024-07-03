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
package com.braintribe.model.processing.traversing.engine.api;

import com.braintribe.model.processing.traversing.api.GmTraversingException;
import com.braintribe.model.processing.traversing.api.GmTraversingVisitor;
import com.braintribe.model.processing.traversing.engine.api.customize.ModelWalkerCustomization;

public interface TraversingConfigurer<T extends TraversingConfigurer<T>> {

	/**
	 * Configures a depth-first walker visitor.
	 * 
	 * @see #customWalk(GmTraversingVisitor)
	 */
	T depthFirstWalk();

	/**
	 * Configures a breadth-first walker visitor.
	 * 
	 * @see #customWalk(GmTraversingVisitor)
	 */
	T breadthFirstWalk();

	/**
	 * Configures custom walker visitor.
	 * <p>
	 * NOTE It is also possible to register a walker with {@link #visitor(GmTraversingVisitor)}, but this method also notifies the engine
	 * builder not to use the default one. If no walker is configured, the engine uses a depth-first walker as the very last visitor by
	 * default.
	 */
	T customWalk(GmTraversingVisitor visitor);

	/**
	 * Add {@link GmTraversingVisitor} to the existing visitors.
	 * 
	 */
	T visitor(GmTraversingVisitor visitor);

	/**
	 * Run all the visitors in the order that they were provided on the given target
	 */
	void doFor(Object target) throws GmTraversingException;

	/** Update the default walker with {@linkModelWalkerCustomization} */
	T customizeDefaultWalker(ModelWalkerCustomization customization);

}
