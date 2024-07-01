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
package com.braintribe.model.processing.oracle.hierarchy;

import static com.braintribe.model.processing.oracle.model.ModelNames.ANIMAL_MODEL;
import static com.braintribe.model.processing.oracle.model.ModelNames.FARM_MODEL;
import static com.braintribe.model.processing.oracle.model.ModelNames.FISH_MODEL;
import static com.braintribe.model.processing.oracle.model.ModelNames.MAMMAL_MODEL;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.oracle.hierarchy.GraphInliner;
import com.braintribe.model.processing.oracle.model.ModelOracleModelProvider;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see GraphInliner
 * 
 * @author peter.gazdik
 */
public class GraphInlinerTest {

	private static GmMetaModel farmModel = ModelOracleModelProvider.farmModel();

	private List<GmMetaModel> inlinedModels;

	@Test
	public void testInliningOrder() throws Exception {
		inlinedModels = GraphInliner.inline(farmModel, GmMetaModel::getDependencies).list;

		assertModelNames(FARM_MODEL, MAMMAL_MODEL, FISH_MODEL, ANIMAL_MODEL, GenericModelTypeReflection.rootModelName);
	}

	private void assertModelNames(String... expectedNames) {
		List<String> actualNames = inlinedModels.stream().map(GmMetaModel::getName).collect(Collectors.toList());
		Assertions.assertThat(actualNames).containsExactly(expectedNames);
	}
}
