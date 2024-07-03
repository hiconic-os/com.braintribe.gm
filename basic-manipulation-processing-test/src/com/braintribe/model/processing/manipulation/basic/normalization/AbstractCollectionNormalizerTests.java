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
package com.braintribe.model.processing.manipulation.basic.normalization;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.fest.assertions.Assertions;
import org.junit.Assert;
import org.junit.Before;

import com.braintribe.cc.lcd.CodingSet;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.manipulation.RemoveManipulation;
import com.braintribe.model.processing.manipulation.testdata.manipulation.TestEntity;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * 
 */
public abstract class AbstractCollectionNormalizerTests extends AbstractNormalizerTests {

	protected TestEntity entity;

	@Override
	@Before
	public void setup() {
		super.setup();

		entity = session.create(TestEntity.T);

		entity.setIntSet(newSet());
		entity.setIntList(newList());
		entity.setIntMap(newMap());

		entity.setSomeSet(newSet());
		entity.setSomeList(newList());
		entity.setSomeMap(newMap());
	}

	@Override
	protected void normalize() {
		performFullNormalization();
	}

	protected void assertRemove(Object... objs) {
		RemoveManipulation m = getManipulation(RemoveManipulation.class);

		Set<Object> keysForRemove = m.getItemsToRemove().keySet();
		Set<?> expected = asSet(objs);

		assertEqual(keysForRemove, expected, "Wrong remove value.");
	}

	protected void assertEqual(Map<?, ?> actual, Map<?, ?> expected, String as) {
		BtAssertions.assertThat(actual).as(as).isNotNull();

		assertEqual(actual.keySet(), expected.keySet(), as + " wrong keys");
		assertEqual(actual.values(), expected.values(), as + " wrong values");
	}

	protected void assertEqual(Collection<?> actual, Collection<?> expected, String as) {
		if (expected == null)
			Assertions.assertThat(actual).isNull();
		else
			Assertions.assertThat(refSafe(actual)).as(as).isEqualTo(refSafe(expected));
	}

	private Set<Object> refSafe(Collection<?> set) {
		Set<Object> result = CodingSet.create(ElementHashingComparator.INSTANCE);
		result.addAll(set);
		return result;
	}

	protected void clear(Collection<?> c) {
		// makes sure the collection is not empty first, so that the clear is tracked
		((Collection<Object>) c).add(1);
		c.clear();
	}

	protected void clear(Map<?, ?> c) {
		// makes sure the collection is not empty first, so that the clear is tracked
		((Map<Object, Object>) c).put(1, 1);
		c.clear();
	}

	protected <T extends PropertyManipulation> T getManipulation(Class<T> clazz) {
		for (AtomicManipulation m : normalizedManipulations)
			if (clazz.isInstance(m))
				return clazz.cast(m);

		Assert.fail("Manipulation not found: " + clazz.getSimpleName());
		return null;
	}

}
