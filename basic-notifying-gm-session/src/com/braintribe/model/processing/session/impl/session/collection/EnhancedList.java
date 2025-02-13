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
package com.braintribe.model.processing.session.impl.session.collection;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.addManipulation;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.clearManipulation;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.newPlainMap;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.removeManipulation;
import static com.braintribe.model.processing.manipulation.basic.tools.ManipulationTools.createInverse;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.collection.ListBase;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.ClearCollectionManipulation;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.RemoveManipulation;
import com.braintribe.model.generic.manipulation.util.ManipulationBuilder;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.generic.tracking.ManipulationListener;

public class EnhancedList<E> extends AbstractList<E> implements ListBase<E>, EnhancedCollection {
	private List<E> delegate;
	private List<ManipulationListener> listeners;
	private ListType listType;
	private LocalEntityProperty owner;
	private GenericEntity entity;
	private boolean incomplete;
	private boolean loaded;
	private final ReentrantLock loadLock = new ReentrantLock();

	public EnhancedList(ListType listType) {
		this(listType, newList(), false);
	}

	public EnhancedList(ListType listType, List<E> delegate) {
		this(listType, delegate, false);
	}

	public EnhancedList(ListType listType, List<E> delegate, boolean absent) {
		this.listType = listType;
		this.delegate = delegate;
		this.incomplete = absent;
		this.loaded = !absent;
	}

	@Override
	public void setCollectionOwner(LocalEntityProperty owner) {
		this.owner = owner;
		this.entity = owner.getEntity();
	}

	@Override
	public LocalEntityProperty getCollectionOwner() {
		return owner;
	}

	@Override
	public ListType type() {
		return listType;
	}

	@Override
	public void setIncomplete(boolean incomplete) {
		this.incomplete = incomplete;
	}

	@Override
	public boolean isIncomplete() {
		return incomplete;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void addManipulationListener(ManipulationListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<>(2);
		}
		listeners.add(listener);
	}

	@Override
	public void removeManipulationListener(ManipulationListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty())
				listeners = null;
		}
	}

	@Override
	public List<E> getDelegate() {
		return delegate;
	}

	@Override
	public boolean contains(Object o) {
		ensureComplete();
		return delegate.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		ensureComplete();
		return delegate.containsAll(c);
	}

	@Override
	public int indexOf(Object o) {
		ensureComplete();
		return delegate.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		ensureComplete();
		return delegate.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		ensureComplete();
		return delegate.lastIndexOf(o);
	}

	@Override
	public int size() {
		ensureComplete();
		return delegate.size();
	}

	@Override
	public Object[] toArray() {
		ensureComplete();
		return delegate.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		ensureComplete();
		return delegate.toArray(a);
	}

	@Override
	public E get(int index) {
		ensureComplete();
		return delegate.get(index);
	}

	// tracking sensitive methods
	@Override
	public void add(int index, E element) {
		ensureComplete();
		delegate.add(index, element);
		if (!isNoticing())
			return;

		AddManipulation manipulation = addManipulation(index, element, owner);
		RemoveManipulation inverseManipulation = removeManipulation(index, element, owner);
		manipulation.linkInverse(inverseManipulation);

		noticeManipulation(manipulation);
	}

	@Override
	public boolean add(E e) {
		add(size(), e);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		addAll(size(), c);
		return true;
	}

	protected GenericModelType getElementType() {
		return listType.getCollectionElementType();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		ensureComplete();
		if (!isNoticing())
			return delegate.addAll(index, c);

		Map<Object, Object> itemsToAdd = newPlainMap();

		int i = index;
		for (E element : c) {
			Integer indexDescriptor = i++;
			Object elementDescriptor = element;
			itemsToAdd.put(indexDescriptor, elementDescriptor);
		}

		AddManipulation manipulation = addManipulation(itemsToAdd, owner);
		RemoveManipulation inverseManipulation = removeManipulation(itemsToAdd, owner);
		manipulation.linkInverse(inverseManipulation);

		delegate.addAll(index, c);

		noticeManipulation(manipulation);

		return true;
	}

	@Override
	public void clear() {
		if (!isNoticing()) {
			delegate.clear();
			incomplete = false;
			loaded = true;
			return;
		}

		ensureComplete();
		Map<Object, Object> items = newPlainMap();

		int i = 0;
		for (E element : delegate) {
			Integer indexDescriptor = i++;
			Object elementDescriptor = element;
			items.put(indexDescriptor, elementDescriptor);
		}

		ClearCollectionManipulation manipulation = clearManipulation(owner);
		AddManipulation inverseManipulation = addManipulation(items, owner);
		manipulation.linkInverse(inverseManipulation);

		delegate.clear();

		noticeManipulation(manipulation);
	}

	@Override
	public E remove(int index) {
		ensureComplete();
		E retVal = delegate.remove(index);

		if (!isNoticing())
			return retVal;

		RemoveManipulation manipulation = removeManipulation(index, retVal, owner);
		AddManipulation inverseManipulation = addManipulation(index, retVal, owner);
		manipulation.linkInverse(inverseManipulation);

		noticeManipulation(manipulation);

		return retVal;
	}

	protected boolean removeAll(Collection<?> c, boolean notContained) {
		ensureComplete();
		List<Manipulation> manipulations = new ArrayList<>();

		int s = -1;
		int e = -1;

		for (int i = size() - 1; i >= 0; i--) {
			E element = get(i);
			boolean found = c.contains(element) ^ notContained;

			if (found) {
				if (s != -1) {
					// continue interval
					s = i;
				} else {
					// start interval
					s = e = i;
				}
			} else {
				if (s != -1) {
					// stop interval
					manipulations.add(_removeRange(s, e + 1));
					s = e = -1;
				}
			}
		}

		if (s != -1) {
			// stop interval
			manipulations.add(_removeRange(s, e + 1));
			s = e = -1;

		}

		if (manipulations.isEmpty()) {
			return false;
		}

		if (isNoticing()) {
			CompoundManipulation manipulation = compound(manipulations);
			noticeManipulation(manipulation);
		}

		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return removeAll(c, false);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return removeAll(c, true);
	}

	@Override
	public void removeRange(int index, int toIndex) {
		ensureComplete();
		Manipulation manipulation = _removeRange(index, toIndex);
		noticeManipulation(manipulation);
	}

	// TODO optimize -> no need to track manipulations all the time - but beware of usages
	protected Manipulation _removeRange(int index, int toIndex) {
		Map<Object, Object> items = newPlainMap();

		for (int i = index; i < toIndex; i++) {
			E element = delegate.get(i);
			Object elementDescriptor = element;
			Integer indexDescriptor = i;
			items.put(indexDescriptor, elementDescriptor);
		}

		RemoveManipulation manipulation = removeManipulation(items, owner);
		AddManipulation inverseManipulation = addManipulation(items, owner);
		manipulation.linkInverse(inverseManipulation);

		delegate.subList(index, toIndex).clear();

		return manipulation;
	}

	@Override
	public E set(int index, E element) {
		ensureComplete();
		E oldElement = delegate.get(index);
		delegate.set(index, element);

		if (!isNoticing())
			return oldElement;

		// remove
		RemoveManipulation remove = removeManipulation(index, oldElement, owner);
		AddManipulation removeInverse = addManipulation(index, oldElement, owner);
		remove.linkInverse(removeInverse);

		// add
		AddManipulation add = addManipulation(index, element, owner);
		RemoveManipulation addInverse = removeManipulation(index, element, owner);
		add.linkInverse(addInverse);

		Manipulation cManipulation = compound(Arrays.<Manipulation> asList(remove, add));
		noticeManipulation(cManipulation);

		return oldElement;
	}

	private CompoundManipulation compound(List<Manipulation> manipulations) {
		CompoundManipulation manipulation = ManipulationBuilder.compound(manipulations);
		CompoundManipulation inverseManipulation = createInverse(manipulation);

		manipulation.linkInverse(inverseManipulation);

		return manipulation;
	}

	public GenericEntity getEntity() {
		return entity;
	}

	protected boolean isNoticing() {
		if (entity != null && entity.session() != null)
			return true;

		return listeners != null;
	}

	private void noticeManipulation(Manipulation manipulation) {
		if (entity != null) {
			GmSession gmSession = entity.session();
			if (gmSession != null) {
				gmSession.noticeManipulation(manipulation);
			}
		}

		if (listeners != null) {
			ManipulationListener[] la = listeners.toArray(new ManipulationListener[listeners.size()]);
			for (ManipulationListener listener : la) {
				listener.noticeManipulation(manipulation);
			}
		}
	}

	private void ensureComplete() {
		if (loaded || GMF.platform().isSingleThreaded()) {
			return;
		}

		ensureCompleteSync();
	}

	private void ensureCompleteSync() {
		if (loaded)
			return;

		loadLock.lock();
		try {
			if (loaded)
				return;

			EnhanceUtil.<List<E>> loadCollectionLazily(entity, owner, value -> delegate.addAll(value));

			loaded = true;
			incomplete = false;
		} finally {
			loadLock.unlock();
		}
	}

	/**
	 * Slightly differs from implementation from {@link AbstractList} - it checks the size of the lists first, and continues
	 * only if it is equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof List))
			return false;

		ensureComplete();

		List<?> l = (List<?>) o;
		if (this.size() != l.size()) {
			return false;
		}

		Iterator<?> it1 = this.iterator();
		Iterator<?> it2 = l.iterator();

		while (it1.hasNext()) {
			Object e1 = it1.next();
			Object e2 = it2.next();

			if (!(e1 == null ? e2 == null : e1.equals(e2))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		ensureComplete();
		return delegate.hashCode();
	}

	@Override
	public String toString() {
		ensureComplete();
		return delegate.toString();
	}

}
