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
package com.braintribe.model.processing.query.test.selection;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.braintribe.model.processing.query.api.stringifier.QuerySelection;
import com.braintribe.model.processing.query.selection.BasicQuerySelectionResolver;

public class SelectQueryPathModeTests extends AbstractSelectionResolverTest {
	
	private BasicQuerySelectionResolver resolver = 
			BasicQuerySelectionResolver.create()
									   .aliasMode()
									   	.path()
									   .shorteningMode()
									   	.simplified();
	
	@Test
	public void testSingleReferenceJoin() {
		List<QuerySelection> selections = stringifyAndResolve(singleReferenceQuery(), resolver);
		
		assertThat(selections).isNotEmpty();
		assertThat(selections).hasSize(4);
		assertThat(selections.get(0).getAlias()).isEqualTo("Person.name");
		assertThat(selections.get(1).getAlias()).isEqualTo("Person.company");
		assertThat(selections.get(2).getAlias()).isEqualTo("Person.company");
		assertThat(selections.get(3).getAlias()).isEqualTo("Person.company.name");
		
	}

	
	@Test
	public void testCollectionReferenceJoin() {


		List<QuerySelection> selections = stringifyAndResolve(collectionReferenceQuery(), resolver);	
		
		assertThat(selections).isNotEmpty();
		assertThat(selections).hasSize(6);
		assertThat(selections.get(0).getAlias()).isEqualTo("Company.persons.name");
		assertThat(selections.get(1).getAlias()).isEqualTo("Company.persons.company");
		assertThat(selections.get(2).getAlias()).isEqualTo("Company");
		assertThat(selections.get(3).getAlias()).isEqualTo("Company.name");
		assertThat(selections.get(4).getAlias()).isEqualTo("Company.persons.localizedString.id");
		assertThat(selections.get(5).getAlias()).isEqualTo("Company.persons.localizedString");

	}

	
	@Test
	public void testFunctions() {
		List<QuerySelection> selections = stringifyAndResolve(functionQuery(), resolver);
		
		assertThat(selections).isNotEmpty();
		assertThat(selections).hasSize(5);
		assertThat(selections.get(0).getAlias()).isEqualTo("Person");
		assertThat(selections.get(1).getAlias()).isEqualTo("count(_Person)");
		assertThat(selections.get(2).getAlias()).isEqualTo("toString(_Person)");
		assertThat(selections.get(3).getAlias()).isEqualTo("avg(_Person.age)");
		assertThat(selections.get(4).getAlias()).isEqualTo("concatenation(_Person, '.', _Person.name, ':', _Person, '.', _Person.companyName)");

		
	}
	

	@Test
	public void testMultipleWildcardSelection() {
		List<QuerySelection> selections = stringifyAndResolve(wildcardsQuery(), resolver);
		
		assertThat(selections).isNotEmpty();
		assertThat(selections).hasSize(4);
		assertThat(selections.get(0).getAlias()).isEqualTo("Person");
		assertThat(selections.get(1).getAlias()).isEqualTo("Company");
		assertThat(selections.get(2).getAlias()).isEqualTo("Company.persons");
		assertThat(selections.get(3).getAlias()).isEqualTo("Company.persons.localizedString");
		//assertThat(selections.get(4).getAlias()).isEqualTo("Company.persons.indexedCompany");
		
	}

}
