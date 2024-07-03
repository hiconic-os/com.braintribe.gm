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
package com.braintribe.model.access.collaboration.persistence.tools;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static java.util.Collections.emptySet;

import java.io.File;
import java.util.Set;
import java.util.function.Supplier;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.EntityReferenceType;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;

/**
 * @author peter.gazdik
 */
public class ModifiedEntitiesSupplier implements Supplier<Set<GenericEntity>> {

	private final File gmmlFile;
	private final ManagedGmSession csaSession;

	public ModifiedEntitiesSupplier(File gmmlFile, ManagedGmSession csaSession) {
		this.gmmlFile = gmmlFile;
		this.csaSession = csaSession;
	}

	@Override
	public Set<GenericEntity> get() {
		if (!gmmlFile.exists())
			return emptySet();

		return new GmmlFileProcessor().run();
	}

	private class GmmlFileProcessor {
		private final Set<GenericEntity> result = newSet();

		public Set<GenericEntity> run() {
			CsaPersistenceTools.parseGmmlFile(gmmlFile, this::visitManipulation);

			return result;
		}

		private void visitManipulation(Manipulation m) {
			switch (m.manipulationType()) {
				case INSTANTIATION:
					// This we can ignore, because later we must encounter a globalId assignment, and we handle that
					return;

				case CHANGE_VALUE:
					visitChangeValueManipulation((ChangeValueManipulation) m);
					return;

				case ACQUIRE:
				case DELETE:
				case ADD:
				case CLEAR_COLLECTION:
				case REMOVE:
					visitManipulationWithOwner((AtomicManipulation) m);
					return;

				default:
					return;
			}
		}

		private void visitChangeValueManipulation(ChangeValueManipulation m) {
			if (GenericEntity.globalId.equals(m.getOwner().getPropertyName()))
				notifyEntity(m.getNewValue());
			else
				visitManipulationWithOwner(m);
		}

		private void visitManipulationWithOwner(AtomicManipulation m) {
			EntityReference ref = (EntityReference) m.manipulatedEntity();

			if (ref.referenceType() == EntityReferenceType.global)
				notifyEntity(ref.getRefId());
		}

		private void notifyEntity(Object _globalId) {
			String globalId = (String) _globalId;

			GenericEntity entity = csaSession.findEntityByGlobalId(globalId);
			if (entity != null)
				result.add(entity);
		}

	}

}
