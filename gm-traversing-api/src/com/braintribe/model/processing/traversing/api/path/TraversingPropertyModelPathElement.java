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
package com.braintribe.model.processing.traversing.api.path;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.IPropertyModelPathElement;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

/**
 * This {@link TraversingPropertyRelatedModelPathElement} addresses the property value itself which in cases of collection properties precedes other
 * {@link TraversingPropertyRelatedModelPathElement}s
 * 
 * @author dirk.scheffler
 * @author pit.steinlin
 * @author peter.gazdik
 */
public class TraversingPropertyModelPathElement extends TraversingPropertyRelatedModelPathElement implements IPropertyModelPathElement {

	private boolean valueResolved;
	private boolean typeResolved;
	private boolean absent;
	private final GenericEntity entity;
	private final Property property;
	private final EntityType<?> entityType;

	public TraversingPropertyModelPathElement(TraversingModelPathElement previous, Object value, GenericModelType type, GenericEntity entity,
			EntityType<?> entityType, Property property, boolean absenceFlag) {
		super(previous, value, type);
		this.entity = entity;
		this.entityType = entityType;
		this.property = property;
		this.absent = absenceFlag;
	}

	public TraversingPropertyModelPathElement(TraversingModelPathElement previous, GenericEntity entity, EntityType<?> entityType, Property property,
			boolean absenceFlag) {
		super(previous);
		this.absent = absenceFlag;
		this.entity = entity;
		this.entityType = entityType;
		this.property = property;
		this.absent = absenceFlag;
	}

	@Override
	public void substituteValue(Object value) {
		super.substituteValue(value);
		valueResolved = true;
		typeResolved = true;
	}

	@Override
	public Property getProperty() {
		return property;
	}

	@Override
	public <T extends GenericEntity> T getEntity() {
		return (T) entity;
	}

	@Override
	public <T extends GenericEntity> EntityType<T> getEntityType() {
		return entityType.cast();
	}

	@Override
	public <T> T getValue() {
		if (!valueResolved) {
			value = getProperty().get(getEntity());
			valueResolved = true;
		}

		return (T) value;
	}

	@Override
	public <T extends GenericModelType> T getType() {
		if (!typeResolved) {
			type = getProperty().getType().getActualType(getValue());
			typeResolved = true;
		}

		return (T) type;
	}

	@Override
	public ModelPathElementType getElementType() {
		return ModelPathElementType.Property;
	}

	public boolean isAbsent() {
		return absent;
	}

	public void setValueResolved(boolean valueResolved) {
		this.valueResolved = valueResolved;
	}

	public void setTypeResolved(boolean typeResolved) {
		this.typeResolved = typeResolved;
	}
}
