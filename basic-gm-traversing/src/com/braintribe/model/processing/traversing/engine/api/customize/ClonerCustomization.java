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
package com.braintribe.model.processing.traversing.engine.api.customize;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.SkipUseCase;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyRelatedModelPathElement;
import com.braintribe.model.processing.traversing.engine.impl.clone.Cloner;

/**
 * Represents possible customizations for a {@link Cloner}.
 */
public interface ClonerCustomization extends PropertyTransferExpert {

	/**
	 * This method returns the intended clone of the {@code instanceToBeCloned}. It is guaranteed that it is never called more than once for
	 * any entity.
	 *
	 * This method CANNOT RETURN <tt>null</tt>.
	 */
	<T extends GenericEntity> T supplyRawClone(T instanceToBeCloned, GmTraversingContext context, TraversingModelPathElement pathElement,
			EntityType<T> entityType);

	/**
	 * Verify if {@link AbsenceInformation} can be resolved (which might trigger a property query, for instance). This method is only
	 * invoked iff {@code absenceInformation} is not <tt>null</tt>.
	 */
	boolean isAbsenceResolvable(GenericEntity instanceToBeCloned, GmTraversingContext context,
			TraversingPropertyModelPathElement propertyPathElement, AbsenceInformation absenceInformation);

	/**
	 * Create {@link AbsenceInformation} that represents a property at an entity. This is used if the {@link SkipUseCase} signifies that
	 * property should be absent and the property value in {@code instanceToBeCloned} was not an {@link AbsenceInformation}.
	 */
	AbsenceInformation createAbsenceInformation(GenericEntity instanceToBeCloned, GmTraversingContext context,
			TraversingPropertyModelPathElement propertyPathElement);

	/**
	 * Post process property related value after cloning. This is a chance to modify the value that is going to be set. This happens before
	 * the {@link #transferProperty} is invoked, thus modifying the cloned value might not be yet attached to a session and thus no
	 * manipulations might be tracked.
	 */
	Object postProcessClonedPropertyRelatedValue(Object clonedValue, GmTraversingContext context,
			TraversingPropertyRelatedModelPathElement pathElement);

}
