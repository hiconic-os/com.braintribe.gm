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
package com.braintribe.model.processing.cmd;

import java.util.function.Supplier;

import org.junit.Test;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.PropertyMetaData;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.model.Person;
import com.braintribe.model.processing.cmd.test.provider.PropertyValueComparatorMdProvider;
import com.braintribe.model.processing.meta.cmd.CmdResolverBuilder;

public class PropertyValueComparatorTests extends MetaDataResolvingTestBase {

	/** @see PropertyValueComparatorMdProvider#addPropertyMdWithStringEqualComparator() */
	@Test
	public void compareStringPropertyWithEqualComparator() {
		PropertyMetaData md;

		md = getMetaData().entityClass(Person.class).property("name").meta(SimplePropertyMetaData.T).exclusive();
		assertNoMd(md);

		Person person = Person.T.create();
		person.setName("foo");

		md = getMetaData().entityClass(Person.class).property("name").entity(person).meta(SimplePropertyMetaData.T).exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, md);
	}

	/** @see PropertyValueComparatorMdProvider#addPropertyMdWithIntGreaterComparator */
	@Test
	public void compareIntPropertyWithExplicitPropertyPathComparator() {
		PropertyMetaData md;

		md = getMetaData().entityClass(Person.class).property("name").meta(SimplePropertyMetaData.T).exclusive();
		assertNoMd(md);

		Person person = Person.T.create();
		person.setAge(1);

		md = getMetaData().entityClass(Person.class).property("name").entity(person).meta(SimplePropertyMetaData.T).exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, md);
	}

	/** @see PropertyValueComparatorMdProvider#addPropertyMdWithCollectionFirstElementEqualComparator */
	@Test
	public void compareListPropertyWithFirstElementComparator() {
		PropertyMetaData md;

		md = getMetaData().entityClass(Person.class).property("name").meta(SimplePropertyMetaData.T).exclusive();
		assertNoMd(md);

		Person friend = Person.T.create();
		friend.setName("foo");

		Person person = Person.T.create();
		person.setName("holder");
		person.getFriends().add(friend);

		md = getMetaData().entityClass(Person.class).property("name").entity(person).useCase("firstElement").meta(SimplePropertyMetaData.T)
				.exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, md);
	}

	/** @see PropertyValueComparatorMdProvider#addPropertyMdWithListSizeComparator */
	@Test
	public void compareListPropertyWithSizeComparator() {
		PropertyMetaData md;

		md = getMetaData().entityClass(Person.class).property("name").meta(SimplePropertyMetaData.T).exclusive();
		assertNoMd(md);

		Person friend = Person.T.create();
		friend.setName("foo");

		Person friend2 = Person.T.create();
		friend2.setName("foo");

		Person person = Person.T.create();
		person.setName("holder");
		person.getFriends().add(friend);
		person.getFriends().add(friend2);

		md = getMetaData().entityClass(Person.class).property("name").entity(person).useCase("size-list").meta(SimplePropertyMetaData.T).exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, md);
	}

	/** @see PropertyValueComparatorMdProvider#addPropertyMdWithSetSizeComparator */
	@Test
	public void compareSetPropertyWithSizeComparator() {
		PropertyMetaData md;

		md = getMetaData().entityClass(Person.class).property("name").meta(SimplePropertyMetaData.T).exclusive();
		assertNoMd(md);

		Person friend = Person.T.create();
		friend.setName("foo");

		Person friend2 = Person.T.create();
		friend2.setName("foo");

		Person person = Person.T.create();
		person.setName("holder");
		person.getOtherFriends().add(friend);
		person.getOtherFriends().add(friend2);

		md = getMetaData().entityClass(Person.class).property("name").entity(person).useCase("size-set").meta(SimplePropertyMetaData.T).exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, md);
	}

	/** @see PropertyValueComparatorMdProvider#addPropertyMdWithMapSizeComparator */
	@Test
	public void compareMapPropertyWithSizeComparator() {
		PropertyMetaData md;

		md = getMetaData().entityClass(Person.class).property("name").meta(SimplePropertyMetaData.T).exclusive();
		assertNoMd(md);

		Person person = Person.T.create();
		person.setName("holder");
		person.getProperties().put("foo", "bar");
		person.getProperties().put("foo2", "bar2");

		md = getMetaData().entityClass(Person.class).property("name").entity(person).useCase("size-map").meta(SimplePropertyMetaData.T).exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, md);
	}

	@Override
	protected Supplier<GmMetaModel> getModelProvider() {
		return new PropertyValueComparatorMdProvider();
	}

	@Override
	protected void setupCmdResolver(CmdResolverBuilder crb) {
		crb.setSessionProvider(Thread::currentThread);
	}
}
