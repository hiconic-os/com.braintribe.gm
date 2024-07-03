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
package com.braintribe.model.access.impls;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Test;

import com.braintribe.model.access.NextPageAware;
import com.braintribe.model.access.PageProvider;
import com.braintribe.model.access.PaginatedIterable;
import com.braintribe.model.access.model.Book;
import com.braintribe.model.generic.GenericEntity;

public class PaginatedIterableTests {

	@Test
	public void testPaginationNull() throws Exception {
		Iterator<?> iterator = nullIterator();
		assertThat(iterator.hasNext()).isFalse();
		try {
			iterator.next();
			throw new Exception("Code must not be reached.");
		} catch (NoSuchElementException e) {
			assertThat(e.getMessage()).contains("no element available");
		}
	}

	@Test
	public void testPaginationEmpty() throws Exception {
		Iterator<?> iterator = emptyIterator();

		assertThat(iterator.hasNext()).isFalse();
		try {
			iterator.next();
			throw new Exception("Code must not be reached.");
		} catch (NoSuchElementException e) {
			assertThat(e.getMessage()).contains("no element available");
		}
	}

	@Test
	public void testOnePageIteration() throws Exception {

		Iterator<?> iterator = iterator(1, 10);

		assertThat(iterator.hasNext()).isTrue();

		int i = 0;
		while (iterator.hasNext()) {
			Object e = iterator.next();
			println(e);
			i++;
		}

		assertThat(i).isEqualTo(10);
	}

	@Test
	public void testMultiplePageIteration() throws Exception {

		Iterator<?> iterator = iterator(5, 10);

		assertThat(iterator.hasNext()).isTrue();

		int i = 0;
		while (iterator.hasNext()) {
			Object e = iterator.next();
			println("Element: " + e);
			i++;
		}

		assertThat(i).isEqualTo(50);
	}

	@Test
	public void testPagedEntityIteration() throws Exception {
		int populationSize = 145;
		int internalPageSize = 8;

		Collection<Book> population = buildBooks(populationSize);
		assertThat(population.size()).isEqualTo(populationSize);

		Iterator<Book> iterator = pagedEntityIterator(population, internalPageSize);

		Set<Integer> bookIds = new HashSet<Integer>();
		while (iterator.hasNext()) {
			Book b = iterator.next();
			println("Book: " + b.getId());
			assertThat(bookIds.add(b.<Long> getId().intValue())).isTrue();
		}

		assertThat(bookIds.size()).isEqualTo(populationSize);
		for (int i = 0; i < populationSize; i++) {
			assertThat(bookIds.remove(i)).isTrue();
		}
		assertThat(bookIds).isEmpty();
	}

	@Test
	public void testNextPageAwareIteration() throws Exception {

		Iterator<?> iterator = nextPageAwareIterator(5, 10);

		assertThat(iterator.hasNext()).isTrue();

		int i = 0;
		while (iterator.hasNext()) {
			Object e = iterator.next();
			println("Element: " + e);
			i++;
		}

		assertThat(i).isEqualTo(50);
	}

	@Test
	public void testEmptySubList() {

		List<Object> x = new ArrayList<>();
		List<Object> s = x.subList(0, 0);

		assertThat(x.size()).isEqualTo(0);
		assertThat(s.size()).isEqualTo(0);

	}

	private Iterator<?> nullIterator() {
		return new PaginatedIterable<String>(() -> null);
	}

	private Iterator<?> emptyIterator() {
		return new PaginatedIterable<String>(() -> Collections.emptyList());
	}

	private Iterator<?> iterator(final int pageCount, final int pageSize) {

		final List<List<Integer>> pages = new ArrayList<List<Integer>>();
		for (int pageCounter = 0; pageCounter < pageCount; pageCounter++) {
			List<Integer> page = new ArrayList<Integer>();
			for (int elementCounter = 0; elementCounter < pageSize; elementCounter++) {
				page.add(elementCounter);
			}
			pages.add(page);
		}

		return new PaginatedIterable<Integer>(new PageProvider<Integer>() {

			private final Iterator<List<Integer>> pagesIterator = pages.iterator();
			private int currentPage = 1;

			@Override
			public Collection<Integer> nextPage() {
				if (pagesIterator.hasNext()) {
					println("*****Providing page: " + (currentPage++) + "************");
					return pagesIterator.next();
				}
				return null;
			}
		});
	}

	private Iterator<?> nextPageAwareIterator(final int pageCount, final int pageSize) {

		final List<List<Integer>> pages = new ArrayList<List<Integer>>();
		for (int pageCounter = 0; pageCounter < pageCount; pageCounter++) {
			List<Integer> page = new ArrayList<Integer>();
			for (int elementCounter = 0; elementCounter < pageSize; elementCounter++) {
				page.add(elementCounter);
			}
			pages.add(page);
		}

		class HasNextAwarePageProvider implements PageProvider<Integer>, NextPageAware {

			private final Iterator<List<Integer>> pagesIterator = pages.iterator();
			private int currentPage = 1;

			@Override
			public Collection<Integer> nextPage() {
				println("*****Providing page: " + (currentPage++) + "************");
				return pagesIterator.next();
			}

			@Override
			public boolean hasNextPage() {
				return pagesIterator.hasNext();
			}

		}

		return new PaginatedIterable<Integer>(new HasNextAwarePageProvider());
	}

	private <T extends GenericEntity> Iterator<T> pagedEntityIterator(final Collection<T> population, final int pageSize) {

		return new PaginatedIterable<T>(

				// This is basically a copy of the PageProvider implementation shown in the DemoAccess
				new PageProvider<T>() {

					// We build an internal used list of entities for the given type.
					private final List<T> internalPopulation = new ArrayList<T>(population);

					private final int internalPageSize = pageSize;
					private int currentPage = 0;
					private boolean end = false;

					@Override
					public Collection<T> nextPage() {
						// We have no more elements to process. Returning null to indicate end.
						if (end) {
							return null;
						}

						// Calculate start and end index based on current page and pageSize.
						int startIndex = currentPage * internalPageSize;
						int endIndex = startIndex + internalPageSize;

						if (endIndex >= internalPopulation.size()) {
							// We reached the last page to provide.
							// Adapt the endIndex and signal end by setting the internal flag.
							endIndex = internalPopulation.size();
							end = true;
						}

						// Increment page counter to be prepared for next iteration.
						currentPage++;

						// Return the sub list (page).
						return internalPopulation.subList(startIndex, endIndex);
					}
				});
	}

	private Collection<Book> buildBooks(int count) {
		Set<Book> books = new HashSet<Book>();

		for (long i = 0; i < count; i++) {
			Book b = Book.T.create();
			b.setId(i);
			books.add(b);
		}
		return books;
	}

	private void println(@SuppressWarnings("unused") Object e) {
		// System.out.println(e);
	}

}
