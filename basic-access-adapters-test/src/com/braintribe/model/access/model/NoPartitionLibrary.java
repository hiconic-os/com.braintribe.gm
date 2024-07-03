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
package com.braintribe.model.access.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * 
 */
public interface NoPartitionLibrary extends StandardIdentifiable {

	EntityType<NoPartitionLibrary> T = EntityTypes.T(NoPartitionLibrary.class);

	String getName();
	void setName(String name);

	Map<String, Book> getBookByIsbn();
	void setBookByIsbn(Map<String, Book> bookByIsbn);

	Set<Book> getBookSet();
	void setBookSet(Set<Book> bookSet);

	List<Book> getBookList();
	void setBookList(List<Book> bookList);

}
