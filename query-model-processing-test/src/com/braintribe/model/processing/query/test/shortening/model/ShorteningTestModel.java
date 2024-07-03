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
package com.braintribe.model.processing.query.test.shortening.model;

import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.util.meta.NewMetaModelGeneration;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * @author peter.gazdik
 */
public class ShorteningTestModel {

	public static final Set<EntityType<?>> types = CollectionTools2.<EntityType<?>> asSet( //
			AmbiguiousName.T, //
			com.braintribe.model.processing.query.test.shortening.model.sub.AmbiguiousName.T //
	);

	public static GmMetaModel raw() {
		return new NewMetaModelGeneration().buildMetaModel("com.bt.test:ShorteningTestModel", types);
	}

}
