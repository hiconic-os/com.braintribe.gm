// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.manipulation.parser.impl.manipulator;

import static com.braintribe.utils.lcd.CollectionTools2.first;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.manipulation.parser.impl.model.Joat;

/**
 * @author peter.gazdik
 */
public class ModifiedGmmlManipulatorTest extends AbstractModifiedGmmlManipulatorTest {

	/**
	 * Standard stringifier GMML for instantiation: <tt>$0=(Joat=com.braintribe.model.manipulation.parser.impl.model.Joat)()</tt>
	 * 
	 * What we test:: <tt>$0=!com.braintribe.model.manipulation.parser.impl.model.Joat</tt>
	 */
	@Test
	public void createWithoutAssigningTypeToVariable() throws Exception {
		gmmlModifier = s -> s.replaceAll("\\(Joat=([\\w.]+)\\)", "!$1"); // replace "(Joat=com.xyz.Joat)" with "!com.xyz.Joat"

		recordStringifyAndApply(session -> {
			Joat joat = session.create(Joat.T);
			joat.setGlobalId("joat1");
		});

		Set<GenericEntity> entities = smood.getEntitiesPerType(GenericEntity.T);

		assertThat(entities).hasSize(1);
		assertThat(first(entities).getGlobalId()).isEqualTo("joat1");
	}

	/**
	 * We are testing the following input (except the comment ends with slash, not backslash, but not possible to write in Java):
	 * 
	 * <pre>
	 * // IGNORED LINE COMMENT
	 *
	 * $0=(Joat=com.braintribe.model.manipulation.parser.impl.model.Joat)()
	 * .globalId= /* IGNORED BLOCK COMMENT * / 'joat1'
	 * </pre>
	 */
	@Test
	public void commentsAreIgnored() throws Exception {
		gmmlModifier = s -> "// IGNORED LINE COMMENT\n" + s.replaceAll("globalId=", "globalId= /* IGNORED BLOCK COMMENT */");

		recordStringifyAndApply(session -> {
			Joat joat = session.create(Joat.T);
			joat.setGlobalId("joat1");
		});

		Set<GenericEntity> entities = smood.getEntitiesPerType(GenericEntity.T);

		assertThat(entities).hasSize(1);
		assertThat(first(entities).getGlobalId()).isEqualTo("joat1");
	}
	
	
}
