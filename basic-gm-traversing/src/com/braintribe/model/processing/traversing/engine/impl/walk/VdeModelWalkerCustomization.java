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
package com.braintribe.model.processing.traversing.engine.impl.walk;

import java.util.AbstractMap;
import java.util.Map;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.Escape;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.path.TraversingListItemModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingMapKeyModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingMapValueModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingSetItemModelPathElement;
import com.braintribe.model.processing.traversing.engine.api.customize.ModelWalkerCustomization;
import com.braintribe.model.processing.vde.evaluator.VDE;
import com.braintribe.model.processing.vde.evaluator.api.builder.VdeContextBuilder;

/**
 * A {@link ModelWalkerCustomization} that will replace each path element that contains a {@link ValueDescriptor} with its real value
 * according to the content provided in {@link VdeContextBuilder}
 *
 */
public class VdeModelWalkerCustomization implements ModelWalkerCustomization {

	private final VdeContextBuilder vdeContext;

	private boolean useEscape = false;

	/**
	 * By default, absence info will not be resolved.
	 * 
	 * @see #isAbsenceResolvable
	 */
	private boolean absenceResolvable = false;

	/**
	 * By default, absence info will not be traversed.
	 * 
	 * @see #traverseAbsentProperty
	 */
	private boolean absenceTraversable = false;

	public VdeModelWalkerCustomization(VdeContextBuilder vdeContext, boolean useEscape) {
		this.vdeContext = vdeContext;
		this.useEscape = useEscape;
	}

	@Override
	public boolean isAbsenceResolvable(GmTraversingContext context, Property property, GenericEntity entity,
			AbsenceInformation absenceInformation) {
		return this.absenceResolvable;
	}

	/**
	 * Sets a fixed result for {@link #isAbsenceResolvable} (unless overridden).
	 */
	public void setAbsenceResolvable(boolean absenceResolvable) {
		this.absenceResolvable = absenceResolvable;
	}

	@Override
	public TraversingModelPathElement substitute(GmTraversingContext context, TraversingModelPathElement pathElement) {

		TraversingModelPathElement result = pathElement;

		Object currentValue = pathElement.getValue();

		if (currentValue instanceof ValueDescriptor && !isWithinEscapedScope(pathElement)) {

			Object evaluatedObject = VDE.evaluate(currentValue, vdeContext);

			// if the VDE evaluated the provided object successfully
			if (evaluatedObject != null && !evaluatedObject.equals(currentValue)) {

				if (evaluatedObject instanceof ValueDescriptor) {
					result = getPathElementWithResolvedValue(pathElement, result, evaluatedObject, useEscape);
				} else {
					result = getPathElementWithResolvedValue(pathElement, result, evaluatedObject, false);
				}

			} else {
				// do nothing, and let the traversing process proceed normally
			}
		}
		return result;
	}

	private boolean isWithinEscapedScope(TraversingModelPathElement pathElement) {
		TraversingModelPathElement previous = pathElement.getPrevious();
		while (previous != null) {
			if (previous.getValue() instanceof Escape)
				return true;
			previous = previous.getPrevious();
		}

		return false;
	}

	private TraversingModelPathElement getPathElementWithResolvedValue(TraversingModelPathElement pathElement,
			TraversingModelPathElement result, Object evaluatedObject, boolean useEscape) {
		if (useEscape) {
			evaluatedObject = VDE.builder().escape(evaluatedObject);
		}

		GenericModelType evaluatedType = GMF.getTypeReflection().getBaseType().getActualType(evaluatedObject);

		switch (pathElement.getElementType()) {
			case EntryPoint:
			case Root:
				// TODO check that this case is never possible
				break;
			case Property:
				TraversingPropertyModelPathElement propertyPath = (TraversingPropertyModelPathElement) pathElement;
				TraversingPropertyModelPathElement resolvedPropertyPath = new TraversingPropertyModelPathElement(propertyPath.getPrevious(),
						evaluatedObject, evaluatedType, propertyPath.getEntity(), propertyPath.getEntityType(), propertyPath.getProperty(),
						propertyPath.isAbsent());
				resolvedPropertyPath.setValueResolved(true);
				resolvedPropertyPath.setTypeResolved(true);
				result = resolvedPropertyPath;
				break;
			case ListItem:
				TraversingListItemModelPathElement listItemPath = (TraversingListItemModelPathElement) pathElement;
				TraversingListItemModelPathElement resolvedlistItemPath = new TraversingListItemModelPathElement(listItemPath.getPrevious(),
						evaluatedObject, evaluatedType, listItemPath.getIndex());
				result = resolvedlistItemPath;
				break;
			case MapKey: {
				TraversingMapKeyModelPathElement mapKeyPath = (TraversingMapKeyModelPathElement) pathElement;
				// Map.Entry<?, ?> mapEntry = mapKeyPath.getMapEntry();

				// TODO verify that this usage of map entry works
				Map.Entry<?, ?> mapEntry = new AbstractMap.SimpleEntry<Object, Object>(evaluatedObject, mapKeyPath.getMapValue());
				TraversingMapKeyModelPathElement resolvedMapKeyPath = new TraversingMapKeyModelPathElement(mapKeyPath.getPrevious(),
						evaluatedObject, evaluatedType, mapKeyPath.getMapValue(), mapKeyPath.getMapValueType(), mapEntry);

				result = resolvedMapKeyPath;
				break;
			}
			case MapValue: {
				TraversingMapValueModelPathElement mapValuePath = (TraversingMapValueModelPathElement) pathElement;
				// Map.Entry<?, ?> mapEntry = mapValuePath.getMapEntry();
				// TODO verify that this usage of map entry works
				// mapEntry.setValue((Object)evaluatedObject);
				TraversingMapValueModelPathElement resolvedMapValuePath = new TraversingMapValueModelPathElement(mapValuePath.getKeyElement());

				result = resolvedMapValuePath;
				break;
			}
			case SetItem:
				TraversingSetItemModelPathElement setItemPath = (TraversingSetItemModelPathElement) pathElement;
				TraversingSetItemModelPathElement resolvedSetItemPath = new TraversingSetItemModelPathElement(setItemPath.getPrevious(),
						evaluatedObject, evaluatedType);
				result = resolvedSetItemPath;
				break;

		}
		return result;
	}

	/** Sets a fixed result for {@link #traverseAbsentProperty} (unless overridden). */
	@Override
	public boolean traverseAbsentProperty(GenericEntity entity, Property property, GmTraversingContext context,
			TraversingModelPathElement pathElement) {
		return absenceTraversable;
	}

	public void setAbsenceTraversable(boolean absenceTraversable) {
		this.absenceTraversable = absenceTraversable;
	}
}
