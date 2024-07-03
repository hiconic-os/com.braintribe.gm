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
package com.braintribe.model.processing.mpc.evaluator.api.builder;

import com.braintribe.model.mpc.ModelPathCondition;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluator;
import com.braintribe.model.processing.mpc.evaluator.api.MpcRegistry;

/**
 * A builder for {@link MpcRegistry} that facilitates its construction and
 * manipulation. It also provides default registry setups.
 *
 */
public interface MpcRegistryBuilder {

	/**
	 * @return A registry with only all the default experts loaded.
	 */
	MpcRegistry defaultSetup();

	/**
	 * Includes the content of one {@link MpcRegistry} into another.
	 * 
	 * @param otherRegistry
	 *            The registry that will be merged
	 * @return An updated registry builder with the contents of otherRegistry
	 */
	MpcRegistryBuilder addRegistry(MpcRegistry otherRegistry);

	/**
	 * Adds the default setup {@see MpcRegistryBuilder#defaultSetup()} to the
	 * existing registry.
	 * 
	 * @return An updated registry builder with the default setup as part of its
	 *         structure
	 */
	MpcRegistryBuilder loadDefaultSetup();

	/**
	 * Adds a custom concrete expert to the context
	 * 
	 * A concrete expert is a VDE expert that matches the provided
	 * valueDescriptor directly.
	 * 
	 * @param mpcType
	 *            A class type that extends ValueDescriptor
	 * @param mpcEvaluator
	 *            A ValueDescriptorEvaluator for the provided class type
	 * @return A registry builder with the new expert added to its concrete
	 *         experts
	 */
	<D extends ModelPathCondition> MpcRegistryBuilder withExpert(Class<D> mpcType, MpcEvaluator<? super D> mpcEvaluator);


	/**
	 * Remove a concrete expert from the {@link MpcRegistry}
	 * 
	 * @param mpcType
	 *            The class that identifies the expert
	 * @return A registry builder with the concrete expert removed from its
	 *         context, if it existed
	 */
	MpcRegistryBuilder removeExpert(Class<? extends ModelPathCondition> mpcType);

	/**
	 * @return A {@link MpcRegistry} as the result of building process.
	 */
	MpcRegistry done();

}
