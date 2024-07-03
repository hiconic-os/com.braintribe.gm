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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.GmSystemInterface;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.type.custom.AbstractEntityType;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * @author peter.gazdik
 */
@GmSystemInterface
public abstract class GmtsEntityStub extends GmtsBaseEntityStub {

	private volatile long runtimeId;

	public GmtsEntityStub() {
		//
	}

	@Override
	public void read(Property p, PropertyValueReceiver pvr) {
		Object fieldValue = p.getDirectUnsafe(this);

		ValueDescriptor vd = VdHolder.getValueDescriptorIfPossible(fieldValue);
		if (vd != null)
			pvr.receiveVd(vd);
		else
			pvr.receive(fieldValue);
	}

	public Object idOrRid() {
		Object id = getId();
		return id != null ? id : "~" + runtimeId;
	}

	@Override
	public long runtimeId() {
		return runtimeId != 0 ? runtimeId : ensureRuntimeId();
	}

	private synchronized long ensureRuntimeId() {
		return runtimeId != 0 ? runtimeId : (runtimeId = RuntimeIdGenerator.nextId());
	}

	// TODO change that entityType is actually implemented by ITW and type delegates to it...
	@Override
	public abstract GenericModelType type();

	public <T extends GenericEntity> AbstractEntityType<T> abstractEntityType() {
		return (AbstractEntityType<T>) type();
	}

	@Override
	public <T extends GenericEntity> EntityType<T> entityType() {
		return (EntityType<T>) type();
	}

	@Override
	public <T extends EntityReference> T reference() {
		return (T) this.entityType().createReference(this, this.getId());
	}

	@Override
	public <T extends EntityReference> T globalReference() {
		return (T) this.entityType().createGlobalReference(this, this.getGlobalId());
	}

	@Override
	public <T> T clone(CloningContext cloningContext) {
		return entityType().clone(cloningContext, this, null);
	}

	@Override
	public void traverse(TraversingContext traversingContext) {
		entityType().traverse(traversingContext, this);
	}

	@Override
	public boolean isVd() {
		return type().isVd();
	}

	@Override
	public String toString() {
		return abstractEntityType().toString(this);
	}

	@Override
	public final String toSelectiveInformation() {
		return type().getSelectiveInformation(this);
	}

	/** Using rawTypes so it is compatible with the sources generated for the GWT compiler. */
	@SuppressWarnings("rawtypes")
	public EvalContext eval(Evaluator evaluator) {
		return evaluator.eval(this);
	}

	@Override
	public GenericEntity deproxy() {
		return this;
	}
}
