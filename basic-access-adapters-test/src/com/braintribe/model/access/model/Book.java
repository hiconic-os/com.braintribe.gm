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
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * 
 */
@ToStringInformation("${title}")
public interface Book extends StandardIdentifiable {

	EntityType<Book> T = EntityTypes.T(Book.class);

	String getIsin();
	void setIsin(String isin);

	String getTitle();
	void setTitle(String title);

	int getPages();
	void setPages(int pages);

	Library getLibrary();
	void setLibrary(Library library);

	Set<String> getCharsUsed();
	void setCharsUsed(Set<String> charsUsed);

	List<String> getWriter();
	void setWriter(List<String> writer);

	Map<String, String> getProperties();
	void setProperties(Map<String, String> properties);

}
