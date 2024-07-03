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
package com.braintribe.model.processing.smood;

import static com.braintribe.utils.lcd.CollectionTools2.acquireSet;
import static com.braintribe.utils.lcd.CollectionTools2.newLinkedMap;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;
import java.util.Set;

import com.braintribe.cc.lcd.CodingMap;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.commons.EntRefHashingComparator;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.GlobalEntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.generic.value.PreliminaryEntityReference;
import com.braintribe.model.processing.manipulator.expert.basic.AbstractManipulatorContext;
import com.braintribe.model.processing.session.api.managed.ManipulationMode;
import com.braintribe.model.processing.session.api.notifying.EntityCreation;
import com.braintribe.model.processing.session.api.notifying.NotifyingGmSession;
import com.braintribe.model.processing.smood.manipulation.AbsentCollectionIgnoringAddManipulator;
import com.braintribe.model.processing.smood.manipulation.AbsentCollectionIgnoringRemoveManipulator;
import com.braintribe.utils.lcd.StringTools;

/**
 * 
 */
public class SmoodManipulatorContext extends AbstractManipulatorContext {

	private final Smood smood;
	private final NotifyingGmSession session;
	private ManipulationMode mode;
	private boolean checkRefereesOnDelete;
	private boolean manifestUnknownEntities;

	private final Map<EntityReference, GenericEntity> instantiations = CodingMap.create(newLinkedMap(), EntRefHashingComparator.INSTANCE);
	private final Map<EntityType<?>, Set<GenericEntity>> manifestations = newMap();

	public SmoodManipulatorContext(Smood smood) {
		this.smood = smood;
		this.session = smood.getGmSession();
		this.changeValueManipulator = smood.getChangeValueManipulator();
		this.deleteManipulator = smood.getDeleteManipulator(DeleteMode.dropReferences);
		this.manifestionManipulator = smood.getManifestationManipulator();
	}

	public Map<EntityReference, GenericEntity> getInstantiations() {
		return instantiations;
	}

	public Map<EntityType<?>, Set<GenericEntity>> getManifestations() {
		return manifestations;
	}

	public void setManipulationMode(ManipulationMode mode) {
		this.mode = mode;
		setValueResolution(mode != ManipulationMode.LOCAL);
	}

	/** @see ManipulationApplicationBuilder#checkRefereesOnDelete(boolean) */
	public void setCheckRefereesOnDelete(boolean checkRefereesOnDelete) {
		this.checkRefereesOnDelete = checkRefereesOnDelete;
	}

	/** @see ManipulationApplicationBuilder#checkRefereesOnDelete(boolean) */
	public boolean getCheckRefereesOnDelete() {
		return checkRefereesOnDelete;
	}

	public void setIgnoreAbsentCollectionManipulations(boolean ignoreAbsentCollectionManipulations) {
		if (ignoreAbsentCollectionManipulations) {
			this.addManipulator = AbsentCollectionIgnoringAddManipulator.INSTANCE;
			this.bulkRemoveFromCollectionManipulator = AbsentCollectionIgnoringRemoveManipulator.INSTANCE;
		}
	}

	public void setManifestUnknownEntities(boolean manifestUnknownEntities) {
		this.manifestUnknownEntities = manifestUnknownEntities;
	}

	@Override
	protected GenericEntity resolveEntity(EntityReference entityReference) {
		// first lookup for entity in the instantiations done on this context
		// TODO add this prelim check to HibernateManipulationContext
		GenericEntity entity = entityReference instanceof PreliminaryEntityReference ? instantiations.get(entityReference) : null;
		if (entity != null)
			return entity;

		if (manifestUnknownEntities) {
			entity = smood.findEntity(entityReference);
			return entity == null ? manifestEntity(entityReference) : entity;

		} else {
			return smood.getEntity(entityReference);
		}
	}

	/**
	 * This method creates a new instance (for remote case), or simply uses the given entity as the desired entity (local case). In both cases, this
	 * method MAKES SURE that the ENTITY IS ATTACHED TO THE SESSION, and therefore (since the session than calls
	 * {@link GmSession#noticeManipulation(Manipulation)}) this entity is ALSO REGISTERED IN THE SMOOD.
	 * <p>
	 * In case the entity has no id, an entry will be registered in the map retrievable via {@link #getInstantiations()} . The only post-processing
	 * these entities might need is assigning an automatic id, if it was desired and the id was not set by some other manipulation, which comes after
	 * the instantiation.
	 */
	@Override
	public GenericEntity createPreliminaryEntity(GenericEntity entityOrReference) {
		EntityReference ref;
		GenericEntity entity;

		if (mode == ManipulationMode.LOCAL) {
			entity = entityOrReference;
			smood.registerEntity(entity, false);

			ref = entity.reference();
			if (ref instanceof PersistentEntityReference)
				return entityOrReference;

		} else if (entityOrReference instanceof EntityReference) {
			ref = (EntityReference) entityOrReference;
			String partition = ref.getRefPartition();

			EntityCreation<?> creation = session.createEntity(ref.valueType()) //
					.raw() //
					.withPartition(partition);

			if (ref instanceof PreliminaryEntityReference) {
				/* All initialization manipulations are tracked, that's how session.create is implemented. Thus, this must use createRaw. */
				entity = creation.preliminary();

			} else if (ref instanceof PersistentEntityReference) {
				assertReferenceTypeMatchesMode(ref, ManipulationMode.REMOTE);
				entity = creation.persistent(ref.getRefId());

			} else if (ref instanceof GlobalEntityReference) {
				assertReferenceTypeMatchesMode(ref, ManipulationMode.REMOTE_GLOBAL);
				entity = creation.global((String) ref.getRefId());

			} else {
				throw new IllegalArgumentException("Unknown EntityReference type " + ref.entityType() + ". Reference: " + ref);
			}

		} else {
			throw new IllegalArgumentException("Invalid entity value for InstantiationManipulation: " + entityOrReference);
		}

		instantiations.put(ref, entity);

		return entity;
	}

	private void assertReferenceTypeMatchesMode(EntityReference ref, ManipulationMode requiredMode) {
		if (mode != requiredMode)
			throw new GenericModelException("Invalid reference for InstantiationManipulation. " + ref.entityType().getShortName()
					+ " can only be used in " + requiredMode + " mode, but not " + mode + ". Reference: " + ref);
	}

	private GenericEntity manifestEntity(EntityReference entityReference) {
		if (!(entityReference instanceof PersistentEntityReference))
			throw new GenericModelException("Cannot manifest non-persistence reference: " + entityReference);

		EntityType<GenericEntity> et = typeReflection.getType(entityReference.getTypeSignature());
		GenericEntity entity = et.createRaw();

		if (!StringTools.isEmpty(entityReference.getRefPartition()))
			entity.setPartition(entityReference.getRefPartition());

		for (Property p : et.getProperties()) {
			if (p.isIdentifier())
				p.set(entity, entityReference.getRefId());
			else if (!p.isPartition())
				p.setAbsenceInformation(entity, GMF.absenceInformation());
		}

		session.attach(entity);

		acquireSet(manifestations, et).add(entity);

		return entity;
	}

	@Override
	public void deleteEntityIfPreliminary(GenericEntity entityOrReference) {
		if (mode == ManipulationMode.LOCAL) {
			/* This whole "local" request ist BS. We simply attach the entity to Smood, so in case it already had an id, it is not part of
			 * instantiations */
			if (entityOrReference.getId() == null)
				instantiations.remove(entityOrReference.reference());

		} else {
			instantiations.remove(entityOrReference);
		}
	}

}
