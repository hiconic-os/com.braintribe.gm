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

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.function.Supplier;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.util.meta.NewMetaModelGeneration;

/**
 * 
 */
public class BasicAccesAdapterTestModelProvider implements Supplier<GmMetaModel> {

	public static final BasicAccesAdapterTestModelProvider INSTANCE = new BasicAccesAdapterTestModelProvider();

	private BasicAccesAdapterTestModelProvider() {
	}

	private static GmMetaModel metaModel = new NewMetaModelGeneration().buildMetaModel("test:BaaTestModel", asList(Book.T, Library.T, NoPartitionLibrary.T));

	@Override
	public GmMetaModel get() {
		return metaModel;
	}

}
