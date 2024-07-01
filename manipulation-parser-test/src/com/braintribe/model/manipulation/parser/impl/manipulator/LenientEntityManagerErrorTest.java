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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.manipulation.parser.impl.model.Joat;
import com.braintribe.model.processing.manipulation.parser.api.MutableGmmlManipulatorParserConfiguration;
import com.braintribe.model.processing.session.api.managed.EntityManager;

/**
 * Similar to {@link LenientManipulatorTest}, but for errors that happen within the underlying {@link EntityManager}.
 * 
 * @see AbstractModifiedGmmlManipulatorTest
 * 
 * @author peter.gazdik
 */
public class LenientEntityManagerErrorTest extends AbstractModifiedGmmlManipulatorTest {

	private static final String stageName = "StageX";

	@Test
	public void duplicateGlobalId() throws Exception {
		gmmlModifier = s -> s.replaceAll("joat2", "joat1");

		recordStringifyAndApply(session -> {
			session.create(Joat.T, "joat1");
			session.create(Joat.T, "joat2");
		});

		Set<GenericEntity> entities = smood.getEntitiesPerType(GenericEntity.T);
		assertThat(entities).hasSize(2);

		assertThat(findEntity("joat1")).isNotNull();
		assertThat(findEntity("gmml://" + stageName + ".copy#1.joat1")).isNotNull();
	}

	private GenericEntity findEntity(String globalId) {
		return smood.findEntityByGlobalId(globalId);
	}

	@Override
	protected MutableGmmlManipulatorParserConfiguration parserConfig() {
		MutableGmmlManipulatorParserConfiguration result = super.parserConfig();
		result.setStageName(stageName);
		return result;
	}

}
