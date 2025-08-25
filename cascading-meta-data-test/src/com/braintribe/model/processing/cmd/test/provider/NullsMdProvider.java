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
package com.braintribe.model.processing.cmd.test.provider;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.cmd.NullsMetaDataResolvingTests;
import com.braintribe.model.processing.cmd.test.model.Color;

/**
 * @see NullsMetaDataResolvingTests
 */
public class NullsMdProvider extends AbstractModelSupplier {

	@Override
	protected void addMetaData() {
		GmMetaModel model = baseMdEditor.getMetaModel();
		model.getEnumTypeMetaData().add(null);
		model.getEnumConstantMetaData().add(null);

		baseMdEditor.onEnumType(Color.class) //
				.configure(enumTypeInfo -> {
					enumTypeInfo.getMetaData().add(null);
					enumTypeInfo.getEnumConstantMetaData().add(null);
				});

		baseMdEditor.onEnumType(Color.class).configure(Color.GREEN, enumConstantInfo -> {
			enumConstantInfo.getMetaData().add(null);
		});

		baseMdEditor.onEntityType(GenericEntity.T).configure(entityTypeinfo -> {
			entityTypeinfo.getMetaData().add(null);
			entityTypeinfo.getPropertyMetaData().add(null);
		});
	}

}
