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
