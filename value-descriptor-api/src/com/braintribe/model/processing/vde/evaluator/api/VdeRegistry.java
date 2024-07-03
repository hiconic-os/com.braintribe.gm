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
package com.braintribe.model.processing.vde.evaluator.api;

import java.util.List;
import java.util.Map;

import com.braintribe.common.lcd.Pair;
import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * This is a registry that allows the evaluation of {@see ValueDescriptor}s. It
 * is comprised of the following it:
 * <ul>
 * 
 * <li>Concrete Experts: A concrete expert is a VDE expert that matches the
 * provided valueDescriptor directly.</li>
 * 
 * <li>Abstract Experts: An abstract expert is a VDE expert that does NOT match
 * the provided valueDescriptor directly, but rather through assignability</li>
 * 
 * 
 * </ul>
 */
public interface VdeRegistry {

	/**
	 * @return A map of all concrete experts, where the key is a valueDescriptor
	 *         class and the value is the VDE expert
	 */
	Map<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>> getConcreteExperts();

	/**
	 * Sets the concrete experts maps in the registry
	 * 
	 * @param concreteExperts
	 *            A map where key is a valueDescriptor class and the value is
	 *            the VDE expert
	 */
	void setConcreteExperts(Map<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>> concreteExperts);

	// TODO check with Dirk if abstract experts should be set instead of list
	/**
	 * @return A list of all the abstract experts, where the contents of the
	 *         list are Pairs of ValueDescriptor class and the VDE expert
	 */
	List<Pair<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>> getAbstractExperts();

	/**
	 * Sets the abstract experts in the registry
	 * 
	 * @param abstractExperts
	 *            A list where the elemets are pairs of ValueDescriptor class
	 *            and the VDE expert
	 */
	void setAbstractExperts(List<Pair<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>> abstractExperts);

	/**
	 * Augment the registry with a concrete expert
	 * 
	 * @param vdType
	 *            type of ValueDescriptor
	 * @param vdEvaluator
	 *            VDE Expert for ValueDescriptor
	 */
	<D extends ValueDescriptor> void putConcreteExpert(Class<D> vdType, ValueDescriptorEvaluator<? super D> vdEvaluator);

	/**
	 * Remove a concrete expert from the registry if it exists
	 * 
	 * @param valueDescriptorClass
	 *            The type of ValueDescriptor
	 */
	void removeConcreteExpert(Class<? extends ValueDescriptor> valueDescriptorClass);

	/**
	 * Augment the registry with an abstract expert
	 * 
	 * @param vdType
	 *            type of ValueDescriptor
	 * @param vdEvaluator
	 *            VDE expert for ValueDescriptor
	 */
	<D extends ValueDescriptor> void putAbstractExpert(Class<D> vdType, ValueDescriptorEvaluator<? super D> vdEvaluator);

	/**
	 * Remove an abstract expert from the registry if it exists
	 * 
	 * @param valueDescriptorClass
	 *            The type of ValueDescriptor
	 */
	void removeAbstractExpert(Class<? extends ValueDescriptor> valueDescriptorClass);

	/**
	 * Reset all the experts 
	 */
	void resetRegistry();

	/**
	 * Merge the content of another registry into the existing content of this
	 * registry
	 * 
	 * @param otherRegistry
	 *            An external registry that will be merged with the current
	 *            content
	 */
	void loadOtherRegistry(VdeRegistry otherRegistry);

}
