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
package com.braintribe.model.generic.manipulation.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.collection.PlainMap;
import com.braintribe.model.generic.manipulation.AbsentingManipulation;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.ClearCollectionManipulation;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.manipulation.ManifestationManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.ManipulationComment;
import com.braintribe.model.generic.manipulation.NormalizedCompoundManipulation;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.RemoveManipulation;
import com.braintribe.model.generic.manipulation.VoidManipulation;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EssentialCollectionTypes;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.value.EntityReference;

/**
 * 
 * @author peter.gazdik
 */
public class ManipulationBuilder {

	public static InstantiationManipulation instantiation(GenericEntity entity) {
		InstantiationManipulation result = InstantiationManipulation.T.create();
		result.setEntity(entity);

		return result;
	}

	public static DeleteManipulation delete(GenericEntity entity, DeleteMode deleteMode) {
		DeleteManipulation result = DeleteManipulation.T.create();
		result.setDeleteMode(deleteMode);
		result.setEntity(entity);

		return result;
	}

	public static AddManipulation add(Object key, Object value, Owner owner) {
		return addManipulation(key, value, owner);
	}

	public static AddManipulation addManipulation(Object key, Object value, Owner owner) {
		return addManipulation(asPlainMap(key, value), owner);
	}

	public static AddManipulation add(Map<Object, Object> itemsToAdd, Owner owner) {
		return addManipulation(itemsToAdd, owner);
	}

	public static AddManipulation addManipulation(Map<Object, Object> itemsToAdd, Owner owner) {
		AddManipulation result = AddManipulation.T.create();
		result.setItemsToAdd(itemsToAdd);
		result.setOwner(owner);

		return result;
	}

	public static ChangeValueManipulation changeValue(Object newValue, Owner owner) {
		ChangeValueManipulation result = ChangeValueManipulation.T.create();
		result.setOwner(owner);
		result.setNewValue(newValue);

		return result;
	}

	public static RemoveManipulation remove(Object key, Object value, Owner owner) {
		return removeManipulation(key, value, owner);
	}

	public static RemoveManipulation removeManipulation(Object key, Object value, Owner owner) {
		return removeManipulation(asPlainMap(key, value), owner);
	}

	public static RemoveManipulation remove(Map<Object, Object> itemsToRemove, Owner owner) {
		return removeManipulation(itemsToRemove, owner);
	}

	public static RemoveManipulation removeManipulation(Map<Object, Object> itemsToRemove, Owner owner) {
		RemoveManipulation result = RemoveManipulation.T.create();
		result.setItemsToRemove(itemsToRemove);
		result.setOwner(owner);

		return result;
	}

	public static ClearCollectionManipulation clear(Owner owner) {
		return clearManipulation(owner);
	}

	public static ClearCollectionManipulation clearManipulation(Owner owner) {
		ClearCollectionManipulation result = ClearCollectionManipulation.T.create();
		result.setOwner(owner);

		return result;
	}

	public static ManifestationManipulation manifestation(GenericEntity entity) {
		ManifestationManipulation result = ManifestationManipulation.T.create();
		result.setEntity(entity);

		return result;
	}

	public static AbsentingManipulation absenting(AbsenceInformation absenceInformation, Owner owner) {
		AbsentingManipulation result = AbsentingManipulation.T.create();
		result.setAbsenceInformation(absenceInformation);
		result.setOwner(owner);

		return result;
	}

	public static CompoundManipulation compound(Manipulation... manipulations) {
		return compound(asPlainList(manipulations));
	}

	private static ListType compManListType = (ListType) CompoundManipulation.T.getProperty(CompoundManipulation.compoundManipulationList).getType();

	private static List<Manipulation> asPlainList(Manipulation[] manipulations) {
		List<Manipulation> l = (List<Manipulation>) (List<?>) compManListType.createPlain();
		l.addAll(Arrays.asList(manipulations));
		return l;
	}

	public static CompoundManipulation compound(List<? extends Manipulation> manipulations) {
		CompoundManipulation result = compound();
		result.setCompoundManipulationList((List<Manipulation>) manipulations);

		return result;
	}

	public static CompoundManipulation compound() {
		return CompoundManipulation.T.create();
	}

	public static NormalizedCompoundManipulation normalizedCompound(List<Manipulation> manipulations) {
		NormalizedCompoundManipulation result = normalizedCompound();
		result.setCompoundManipulationList(manipulations);

		return result;
	}

	public static NormalizedCompoundManipulation normalizedCompound() {
		return NormalizedCompoundManipulation.T.create();
	}

	public static VoidManipulation voidManipulation() {
		return VoidManipulation.T.create();
	}

	public static EntityProperty entityProperty(EntityReference ref, String propertyName) {
		EntityProperty result = EntityProperty.T.create();
		result.setReference(ref);
		result.setPropertyName(propertyName);

		return result;
	}

	public static LocalEntityProperty localEntityProperty(GenericEntity entity, String propertyName) {
		LocalEntityProperty result = LocalEntityProperty.T.create();
		result.setEntity(entity);
		result.setPropertyName(propertyName);

		return result;
	}

	public static ManipulationComment comment(String text) {
		ManipulationComment result = ManipulationComment.T.create();
		result.setText(text);

		return result;
	}

	public static ManipulationComment comment(String text, Date date) {
		ManipulationComment result = ManipulationComment.T.createRaw();
		result.setText(text);
		result.setDate(date);

		return result;
	}

	public static Map<Object, Object> asPlainMap(Object key, Object value) {
		PlainMap<Object, Object> m = newPlainMap();
		m.put(key, value);
		return m;
	}

	public static PlainMap<Object, Object> newPlainMap() {
		return new PlainMap<>(EssentialCollectionTypes.TYPE_MAP);
	}

}
