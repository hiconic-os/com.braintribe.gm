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
package com.braintribe.model.processing.session.interceptors;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Iterator;

import org.junit.Test;

import com.braintribe.model.processing.session.test.data.Person;

/**
 * 
 * @author peter.gazdik
 */
public class CollectionTrackingTests extends AbstractSessionBasedTest {

	@Test
	public void add() throws Exception {
		Person p = newPerson("john");

		p.getFriendList().add(p);
		p.getFriendList().add(p);
		p.getFriendSet().add(p);
		p.getFriendMap().put("me", p);

		session.commit();

		p = querySinglePerson();

		assertThat(p.getFriendList()).isEqualTo(asList(p, p));
		assertThat(p.getFriendSet()).isEqualTo(asSet(p));
		assertThat(p.getFriendMap()).isEqualTo(asMap("me", p));
	}

	@Test
	public void addAll() throws Exception {
		Person p = newPerson("john");

		p.getFriendList().addAll(asList(p, p));
		p.getFriendSet().addAll(asSet(p));
		p.getFriendMap().putAll(asMap("me", p));

		session.commit();

		p = querySinglePerson();

		assertThat(p.getFriendList()).isEqualTo(asList(p, p));
		assertThat(p.getFriendSet()).isEqualTo(asSet(p));
		assertThat(p.getFriendMap()).isEqualTo(asMap("me", p));
	}

	@Test
	public void remove() throws Exception {
		add();

		Person p = querySinglePerson();

		p.getFriendList().remove(0);
		p.getFriendList().remove(p);
		p.getFriendSet().remove(p);
		p.getFriendMap().remove("me");

		lastNewSession.commit();

		p = querySinglePerson();

		assertThat(p.getFriendList()).isEmpty();
		assertThat(p.getFriendSet()).isEmpty();
		assertThat(p.getFriendMap()).isEmpty();
	}

	@Test
	public void removeAll() throws Exception {
		add();

		Person p = querySinglePerson();

		p.getFriendList().removeAll(asList(p, p));
		p.getFriendSet().removeAll(asSet(p));

		lastNewSession.commit();

		p = querySinglePerson();

		assertThat(p.getFriendList()).isEmpty();
		assertThat(p.getFriendSet()).isEmpty();
	}

	@Test
	public void iteratorRemove() throws Exception {
		add();

		Person p = querySinglePerson();

		clearWithIterator(p.getFriendList());
		clearWithIterator(p.getFriendSet());
		clearWithIterator(p.getFriendMap().entrySet());

		lastNewSession.commit();

		p = querySinglePerson();

		assertThat(p.getFriendList()).isEmpty();
		assertThat(p.getFriendSet()).isEmpty();
		assertThat(p.getFriendMap()).isEmpty();
	}

	/**
	 * @see #iteratorRemove()
	 */
	@Test
	public void iteratorRemove_SpecialCase_MapKeySet() throws Exception {
		add();

		Person p = querySinglePerson();
		clearWithIterator(p.getFriendMap().keySet());

		lastNewSession.commit();

		p = querySinglePerson();
		assertThat(p.getFriendMap()).isEmpty();
	}

	/**
	 * @see #iteratorRemove()
	 */
	@Test
	public void iteratorRemove_SpecialCase_MapValues() throws Exception {
		add();

		Person p = querySinglePerson();
		clearWithIterator(p.getFriendMap().values());

		lastNewSession.commit();

		p = querySinglePerson();
		assertThat(p.getFriendMap()).isEmpty();
	}

	private void clearWithIterator(Iterable<?> iterable) {
		Iterator<?> it = iterable.iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}

	@Test
	public void clear() throws Exception {
		add();

		Person p = querySinglePerson();

		p.getFriendList().clear();
		p.getFriendSet().clear();
		p.getFriendMap().clear();

		lastNewSession.commit();

		p = querySinglePerson();

		assertThat(p.getFriendList()).isEmpty();
		assertThat(p.getFriendSet()).isEmpty();
		assertThat(p.getFriendMap()).isEmpty();
	}
}
