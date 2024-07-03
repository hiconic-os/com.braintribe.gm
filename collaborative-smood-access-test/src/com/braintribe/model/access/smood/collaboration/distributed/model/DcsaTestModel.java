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
package com.braintribe.model.access.smood.collaboration.distributed.model;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.List;
import java.util.stream.Collectors;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.FileSystemSource;
import com.braintribe.model.util.meta.NewMetaModelGeneration;

/**
 * @author peter.gazdik
 */
public class DcsaTestModel {

	public static final String name = "test:DcsaTestModel";

	// @formatter:off
	public static final List<EntityType<?>> types = asList(
			DcsaEntity.T,
			DcsaEntityPointer.T
	);

	public static final List<EntityType<?>> depTypes = asList(
			Resource.T,
			FileSystemSource.T
	);

	// @formatter:off

	public static GmMetaModel raw() {
		return new NewMetaModelGeneration().buildMetaModel(name, types, deps());
	}

	private static List<GmMetaModel> deps() {
		return depTypes.stream() //
				.map(EntityType::getModel) //
				.map(m -> m.<GmMetaModel>getMetaModel()) //
				.collect(Collectors.toList());
	}

	
}
