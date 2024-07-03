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
package com.braintribe.model.processing.vde.evaluator.api.builder;

import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeRegistry;

/**
 * A builder for {@link VdeRegistry} that facilitates its construction and
 * manipulation. It also provides default registry setups.
 *
 */
public interface VdeRegistryBuilder {

	/**
	 * @return A registry with only all the default concrete experts loaded. The
	 *         abstract experts and the cache are both empty.
	 */
	VdeRegistry defaultSetup();

	/**
	 * Includes the content of one {@link VdeRegistry} into another.
	 * 
	 * @param otherRegistry
	 *            The registry that will be merged
	 * @return An updated registry builder with the contents of otherRegistry
	 */
	VdeRegistryBuilder addRegistry(VdeRegistry otherRegistry);

	/**
	 * Adds the default setup {@see VdeRegistryBuilder#defaultSetup()} to the
	 * existing registry.
	 * 
	 * @return An updated registry builder with the default setup as part of its
	 *         structure
	 */
	VdeRegistryBuilder loadDefaultSetup();

	/**
	 * Adds a custom concrete expert to the context
	 * 
	 * A concrete expert is a VDE expert that matches the provided
	 * valueDescriptor directly.
	 * 
	 * @param vdType
	 *            A class type that extends ValueDescriptor
	 * @param vdEvaluator
	 *            A ValueDescriptorEvaluator for the provided class type
	 * @return A registry builder with the new expert added to its concrete
	 *         experts
	 */
	<D extends ValueDescriptor> VdeRegistryBuilder withConcreteExpert(Class<D> vdType, ValueDescriptorEvaluator<? super D> vdEvaluator);

	/**
	 * Adds a custom abstract expert to the context
	 * 
	 * An abstract expert is a VDE expert that does NOT match the provided
	 * valueDescriptor directly, but rather through assignability
	 * 
	 * @param vdType
	 *            A class type that extends ValueDescriptor
	 * @param vdEvaluator
	 *            A ValueDescriptorEvaluator for the provided class type
	 * @return A registry builder with the new expert added to its abstract
	 *         experts
	 */
	<D extends ValueDescriptor> VdeRegistryBuilder withAbstractExpert(Class<D> vdType, ValueDescriptorEvaluator<? super D> vdEvaluator);

	/**
	 * Remove an abstract expert from the {@link VdeRegistry}
	 * 
	 * @param valueDescriptorClass
	 *            The abstract class that identifies the expert
	 * @return A registry builder with the abstract expert removed from its
	 *         content, if it existed
	 */
	VdeRegistryBuilder removeAbstractExpert(Class<? extends ValueDescriptor> valueDescriptorClass);

	/**
	 * Remove a concrete expert from the {@link VdeRegistry}
	 * 
	 * @param valueDescriptorClass
	 *            The class that identifies the expert
	 * @return A registry builder with the concrete expert removed from its
	 *         context, if it existed
	 */
	VdeRegistryBuilder removeConcreteExpert(Class<? extends ValueDescriptor> valueDescriptorClass);

	/**
	 * @return A {@link VdeRegistry} as the result of building process.
	 */
	VdeRegistry done();

}
