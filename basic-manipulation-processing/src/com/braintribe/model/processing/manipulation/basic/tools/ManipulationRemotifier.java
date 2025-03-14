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
package com.braintribe.model.processing.manipulation.basic.tools;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.compound;
import static com.braintribe.utils.lcd.CollectionTools2.newLinkedMap;
import static com.braintribe.utils.lcd.CollectionTools2.newLinkedSet;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.ClearCollectionManipulation;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.manipulation.RemoveManipulation;
import com.braintribe.model.generic.manipulation.util.ManipulationBuilder;
import com.braintribe.model.generic.pr.criteria.RootCriterion;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.generic.value.EntityReference;

/**
 * Turns a "local" manipulation into a remote one. NOTE that there is no check whether given manipulations are remote already, so this must never be
 * called with remote manipulations as parameter.
 */
public class ManipulationRemotifier {

	public static ManipulationRemotifierBuilder with(Manipulation manipulation) {
		return new BuilderImpl(manipulation);
	}

	public static Manipulation remotify(Manipulation m) {
		return with(m).remotify();
	}

	public static Manipulation remotify(Manipulation m, boolean remotifyEnums) {
		return with(m).remotifyEnums(remotifyEnums).remotify();
	}

	public static Manipulation remotify(Manipulation m, Map<GenericEntity, EntityReference> initialReferences) {
		return with(m).initialReferences(initialReferences).remotify();
	}

	public static Manipulation remotify(Manipulation m, Map<GenericEntity, EntityReference> initialReferences, boolean remotifyEnums) {
		return with(m).remotifyEnums(remotifyEnums).initialReferences(initialReferences).remotify();
	}

	private static CompoundManipulation injectReferencesHelper(BuilderImpl builderImpl) {
		List<AtomicManipulation> linearizedManipulations = builderImpl.manipulation.inline();

		List<Manipulation> processedManipulations = processManipulations(linearizedManipulations, builderImpl);

		return compound(processedManipulations);
	}

	private static List<Manipulation> processManipulations(List<AtomicManipulation> list, BuilderImpl b) {
		ReferenceResolver referenceResolver = new ReferenceResolver(list, b.initialReferences, b.globalReferences);
		AtomicManipulationRemotifier remotifier = new AtomicManipulationRemotifier(referenceResolver, b.remotifyEnums);

		List<Manipulation> result = newList(list.size());

		for (AtomicManipulation am : list) {
			Manipulation processedManipulation = remotifier.remotify(am);
			if (processedManipulation != null)
				result.add(processedManipulation);
		}

		return result;
	}

	private static class AtomicManipulationRemotifier {

		private final ReferenceResolver referenceResolver;
		private final boolean remotifyEnums;

		public AtomicManipulationRemotifier(ReferenceResolver referenceResolver, boolean remotifyEnums) {
			this.referenceResolver = referenceResolver;
			this.remotifyEnums = remotifyEnums;
		}

		@SuppressWarnings("incomplete-switch")
		private AtomicManipulation remotify(AtomicManipulation am) {
			switch (am.manipulationType()) {
				/* These manipulations are actually not expected here. They only make sense as local manipulations. */
				case ABSENTING:
				case MANIFESTATION:
					return null;
			}

			AtomicManipulation result = cloneTopLevel(am);
			remotifyOwnerAndValue(result);
			result.setInverseManipulation(null);

			referenceResolver.onLocalManipulationProcessed(am);

			return result;
		}

		@SuppressWarnings("incomplete-switch")
		private AtomicManipulation remotifyOwnerAndValue(AtomicManipulation am) {
			switch (am.manipulationType()) {
				case ADD:
					return remotify((AddManipulation) am);
				case CHANGE_VALUE:
					return remotify((ChangeValueManipulation) am);
				case CLEAR_COLLECTION:
					return remotify((ClearCollectionManipulation) am);
				case DELETE:
					return remotify((DeleteManipulation) am);
				case INSTANTIATION:
					return remotify((InstantiationManipulation) am);
				case REMOVE:
					return remotify((RemoveManipulation) am);
			}
			throw new IllegalArgumentException("Cannot remotify manipulation '" + am + "' of type " + am.manipulationType());
		}

		private AtomicManipulation remotify(AddManipulation am) {
			am.setItemsToAdd(remotifyMap(am.getItemsToAdd()));
			return remotifyPropertyManipulation(am);
		}

		private AtomicManipulation remotify(ChangeValueManipulation am) {
			am.setNewValue(remotifyValueOrCollection(am.getNewValue()));
			return remotifyPropertyManipulation(am);
		}

		private AtomicManipulation remotify(ClearCollectionManipulation am) {
			return remotifyPropertyManipulation(am);
		}

		private AtomicManipulation remotify(DeleteManipulation am) {
			am.setEntity(referenceResolver.getReference(am.getEntity()));
			return am;
		}

		private AtomicManipulation remotify(InstantiationManipulation am) {
			am.setEntity(referenceResolver.getReference(am.getEntity()));
			return am;
		}

		private AtomicManipulation remotify(RemoveManipulation am) {
			am.setItemsToRemove(remotifyMap(am.getItemsToRemove()));
			return remotifyPropertyManipulation(am);
		}

		private AtomicManipulation remotifyPropertyManipulation(PropertyManipulation am) {
			am.setOwner(remotifyOwner(am.getOwner()));
			return am;
		}

		private Object remotifyValueOrCollection(Object value) {
			if (value instanceof Collection)
				return remotifyCollection((Collection<?>) value, (value instanceof Set) ? newLinkedSet() : newList());

			if (value instanceof Map)
				return remotifyMap((Map<?, ?>) value);

			return remotifyValue(value);
		}

		private Object remotifyCollection(Collection<?> oldValues, Collection<Object> newValues) {
			for (Object oldValue : oldValues) {
				newValues.add(remotifyValue(oldValue));
			}

			return newValues;
		}

		private Map<Object, Object> remotifyMap(Map<?, ?> localMap) {
			Map<Object, Object> remotifiedMap = newLinkedMap();

			for (Entry<?, ?> entry : localMap.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();

				remotifiedMap.put(remotifyValue(key), remotifyValue(value));

			}

			return remotifiedMap;
		}

		private Object remotifyValue(Object value) {
			if (value instanceof GenericEntity)
				return referenceResolver.getReference((GenericEntity) value);

			if (value instanceof Enum<?> && remotifyEnums)
				return referenceResolver.getReference((Enum<?>) value);

			return value;
		}

		private Owner remotifyOwner(Owner owner) {
			if (owner instanceof LocalEntityProperty) {
				LocalEntityProperty local = (LocalEntityProperty) owner;

				EntityReference reference = referenceResolver.getReference(local.getEntity());

				return ManipulationBuilder.entityProperty(reference, local.getPropertyName());
			}

			return owner;
		}
	}

	private static AtomicManipulation cloneTopLevel(AtomicManipulation am) {
		return (AtomicManipulation) am.entityType().clone(new TopLevelPropertyCopyingContext(), am, StrategyOnCriterionMatch.reference);
	}

	/** Context which copies all the properties there are. */
	private static class TopLevelPropertyCopyingContext extends StandardCloningContext {
		@Override
		public boolean isTraversionContextMatching() {
			return !(getTraversingStack().peek() instanceof RootCriterion);
		}
	}

	// ###########################################################
	// ## . . . . . . ManipulationRemotifierBuilder . . . . . . ##
	// ###########################################################

	private static class BuilderImpl implements ManipulationRemotifierBuilder {
		final Manipulation manipulation;

		boolean globalReferences;
		Map<GenericEntity, EntityReference> initialReferences;
		boolean remotifyEnums;

		public BuilderImpl(Manipulation manipulation) {
			this.manipulation = manipulation;
		}

		@Override
		public ManipulationRemotifierBuilder globalReferences(boolean globalReferences) {
			this.globalReferences = globalReferences;
			return this;
		}

		@Override
		public ManipulationRemotifierBuilder initialReferences(Map<GenericEntity, EntityReference> initialReferences) {
			this.initialReferences = initialReferences;
			return this;
		}

		@Override
		public ManipulationRemotifierBuilder remotifyEnums(boolean remotifyEnums) {
			this.remotifyEnums = remotifyEnums;
			return this;
		}

		@Override
		public Manipulation remotify() {
			return injectReferencesHelper(this);
		}
	}
}
