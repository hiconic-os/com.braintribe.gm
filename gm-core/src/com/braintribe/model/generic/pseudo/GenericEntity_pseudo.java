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
package com.braintribe.model.generic.pseudo;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CloningContext;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyValueReceiver;
import com.braintribe.model.generic.reflection.TraversingContext;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.meta.GmMetaModel;

/**
 * Very special class used as a base when we need classes for our entities. One special example is InstantTypeWeaving
 * which needs instances of {@link GmMetaModel} (and co.) as input for actual weaving, so we simply need instances of
 * some entities before we weave the first entity.
 * 
 * NOTE that this base class only supports the two special properties from {@link GenericEntity} and not any other of
 * the inherited methods. Those are also not intended to be implemented in the future.
 * 
 * @author peter.gazdik
 */
public abstract class GenericEntity_pseudo implements GenericEntity {

	private Object id;
	private String partition;
	private String globalId;

	@Override
	public <T> T getId() {
		return (T) id;
	}

	@Override
	public void setId(Object id) {
		this.id = id;
	}

	@Override
	public String getPartition() {
		return partition;
	}

	@Override
	public void setPartition(String partition) {
		this.partition = partition;
	}

	@Override
	public String getGlobalId() {
		return globalId;
	}

	@Override
	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	// ################################################
	// ## . . . . . . non property methods . . . . . ##
	// ################################################

	@Override
	public boolean isEnhanced() {
		return false;
	}

	@Override
	public void write(Property p, Object value) {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.write' is not supported!");
	}

	@Override
	public Object read(Property p) {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.read' is not supported!");
	}

	@Override
	public void writeVd(Property p, ValueDescriptor value) {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.writeVd' is not supported!");
	}

	@Override
	public ValueDescriptor readVd(Property p) {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.readVd' is not supported!");
	}

	@Override
	public void read(Property p, PropertyValueReceiver pvr) {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.read' is not supported!");
	}

	@Override
	public GenericModelType type() {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.type' is not supported!");
	}

	@Override
	public <T extends GenericEntity> EntityType<T> entityType() {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.entityType' is not supported!");
	}

	@Override
	public <T extends EntityReference> T reference() {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.reference' is not supported!");
	}

	@Override
	public <T extends EntityReference> T globalReference() {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.globalReference' is not supported!");
	}

	@Override
	public long runtimeId() {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.runtimeId' is not supported!");
	}

	@Override
	public GmSession session() {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.session' is not supported!");
	}

	@Override
	public void attach(GmSession session) {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.attach' is not supported!");
	}

	@Override
	public GmSession detach() {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.detach' is not supported!");
	}

	@Override
	public <T> T clone(CloningContext cloningContext) {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.clone' is not supported!");
	}

	@Override
	public void traverse(TraversingContext traversingContext) {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.traverse' is not supported!");
	}

	@Override
	public String toSelectiveInformation() {
		throw new UnsupportedOperationException("Method 'GenericEntity_pseudo.toSelectiveInformation' is not implemented yet!");
	}

	@Override
	public boolean isVd() {
		return false;
	}
}
