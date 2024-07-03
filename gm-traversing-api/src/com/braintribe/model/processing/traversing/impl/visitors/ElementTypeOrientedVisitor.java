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
package com.braintribe.model.processing.traversing.impl.visitors;

import com.braintribe.model.generic.path.api.IListItemModelPathElement;
import com.braintribe.model.generic.path.api.IMapKeyModelPathElement;
import com.braintribe.model.generic.path.api.IMapValueModelPathElement;
import com.braintribe.model.generic.path.api.IPropertyModelPathElement;
import com.braintribe.model.generic.path.api.IRootModelPathElement;
import com.braintribe.model.generic.path.api.ISetItemModelPathElement;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.GmTraversingException;
import com.braintribe.model.processing.traversing.api.GmTraversingVisitor;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;

public abstract class ElementTypeOrientedVisitor implements GmTraversingVisitor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onElementEnter(GmTraversingContext context, TraversingModelPathElement pathElement) throws GmTraversingException {
		switch (pathElement.getElementType()) {
		case ListItem:
			onListItemEnter(context, (IListItemModelPathElement) pathElement);
			break;
		case MapKey:
			onMapKeyEnter(context, (IMapKeyModelPathElement) pathElement);
			break;
		case MapValue:
			onMapValueEnter(context, (IMapValueModelPathElement) pathElement);
			break;
		case Property:
			onPropertyEnter(context, (IPropertyModelPathElement) pathElement);
			break;
		case Root:
			onRootEnter(context, (IRootModelPathElement) pathElement);
			break;
		case SetItem:
			onSetItemEnter(context, (ISetItemModelPathElement) pathElement);
			break;
		default:
			throw new GmTraversingException("Unknown element type: " + pathElement.getElementType());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onElementLeave(GmTraversingContext context, TraversingModelPathElement pathElement) throws GmTraversingException {
		switch (pathElement.getElementType()) {
		case ListItem:
			onListItemLeave(context, (IListItemModelPathElement) pathElement);
			break;
		case MapKey:
			onMapKeyLeave(context, (IMapKeyModelPathElement) pathElement);
			break;
		case MapValue:
			onMapValueLeave(context, (IMapValueModelPathElement) pathElement);
			break;
		case Property:
			onPropertyLeave(context, (IPropertyModelPathElement) pathElement);
			break;
		case Root:
			onRootLeave(context, (IRootModelPathElement) pathElement);
			break;
		case SetItem:
			onSetItemLeave(context, (ISetItemModelPathElement) pathElement);
			break;
		default:
			throw new GmTraversingException("Unknown element type: " + pathElement.getElementType());
		}
	}

	@SuppressWarnings("unused")
	protected void onPropertyEnter(GmTraversingContext context, IPropertyModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onPropertyLeave(GmTraversingContext context, IPropertyModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onRootEnter(GmTraversingContext context, IRootModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onRootLeave(GmTraversingContext context, IRootModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onListItemEnter(GmTraversingContext context, IListItemModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onListItemLeave(GmTraversingContext context, IListItemModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onSetItemEnter(GmTraversingContext context, ISetItemModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onSetItemLeave(GmTraversingContext context, ISetItemModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onMapKeyEnter(GmTraversingContext context, IMapKeyModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onMapKeyLeave(GmTraversingContext context, IMapKeyModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onMapValueEnter(GmTraversingContext context, IMapValueModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

	@SuppressWarnings("unused")
	protected void onMapValueLeave(GmTraversingContext context, IMapValueModelPathElement pathElement) {
		// intentionally left blank; can be implemented by sub-class
	}

}
