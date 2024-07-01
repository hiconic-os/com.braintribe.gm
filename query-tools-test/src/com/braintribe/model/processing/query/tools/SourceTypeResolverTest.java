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
package com.braintribe.model.processing.query.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.Source;

/**
 * Tests for {@link SourceTypeResolver}
 * 
 * @author peter.gazdik
 */
public class SourceTypeResolverTest {

	private GenericModelType type;

	@Test
	public void correctCollectionType() throws Exception {
		From modelFrom = from(GmMetaModel.T);
		PropertyOperand depsPo = po(modelFrom, "dependencies");

		type = SourceTypeResolver.resolvePropertyType(depsPo, false);
		assertThat(type).isInstanceOf(CollectionType.class);

		type = SourceTypeResolver.resolvePropertyType(depsPo, true);
		assertThat(type).isSameAs(GmMetaModel.T);

		// wrapping in a PO with no propertyName - there was a bug where this didn't work
		
		Join depsJoin = join(modelFrom, "dependencies");
		PropertyOperand indirectDepsPo = po(depsJoin, null);

		type = SourceTypeResolver.resolvePropertyType(indirectDepsPo, false);
		assertThat(type).isInstanceOf(CollectionType.class);

		type = SourceTypeResolver.resolvePropertyType(indirectDepsPo, true);
		assertThat(type).isSameAs(GmMetaModel.T);
	}

	private static From from(EntityType<?> et) {
		From result = From.T.create();
		result.setEntityTypeSignature(et.getTypeSignature());

		return result;
	}

	private static Join join(Source source, String property) {
		Join result = Join.T.create();
		result.setSource(source);
		result.setProperty(property);

		return result;
	}

	private static PropertyOperand po(Source source, String propertyName) {
		PropertyOperand result = PropertyOperand.T.create();
		result.setSource(source);
		result.setPropertyName(propertyName);

		return result;
	}
}
