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
package com.braintribe.model.io.metamodel.testbase;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.data.MetaData;

/**
 * 
 */
public class EnumTypeBuilder {

	private final GmEnumType type;
	private final Map<String, GmEnumConstant> constantByName = newMap();

	public EnumTypeBuilder(String typeSignature) {
		type = GmEnumType.T.create("type:" + typeSignature);
		type.setTypeSignature(typeSignature);
	}

	public EnumTypeBuilder addMd(MetaData... md) {
		type.getMetaData().addAll(Arrays.asList(md));
		return this;
	}

	public EnumTypeBuilder setConstants(String... values) {
		List<GmEnumConstant> constants = new ArrayList<GmEnumConstant>();

		for (String s : values) {
			GmEnumConstant c = GmEnumConstant.T.create("enum:" + type.getTypeSignature() + "/" + s);
			c.setName(s);
			c.setDeclaringType(type);

			constants.add(c);
			constantByName.put(s, c);
		}

		type.setConstants(constants);

		return this;
	}

	public EnumTypeBuilder addConstantMd(String constName, MetaData... md) {
		constantByName.get(constName).getMetaData().addAll(asList(md));
		return this;
	}

	public GmEnumType create() {
		return type;
	}

}
