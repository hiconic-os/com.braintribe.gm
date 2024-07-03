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
package com.braintribe.model.processing.meta.oracle;

import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.List;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.processing.meta.oracle.flat.FlatEnumConstant;

/**
 * @author peter.gazdik
 */
public class BasicEnumConstantOracle implements EnumConstantOracle {

	private final BasicEnumTypeOracle enumOracle;
	private final FlatEnumConstant flatEnumConstant;

	public BasicEnumConstantOracle(BasicEnumTypeOracle entityOracle, FlatEnumConstant flatProperty) {
		this.enumOracle = entityOracle;
		this.flatEnumConstant = flatProperty;
	}

	@Override
	public EnumTypeOracle getEnumTypeOracle() {
		return enumOracle;
	}

	@Override
	public GmEnumConstant asGmEnumConstant() {
		return flatEnumConstant.gmEnumConstant;
	}

	@Override
	public Enum<?> asEnum() {
		return enumOracle.<EnumType> asType().getEnumValue(flatEnumConstant.gmEnumConstant.getName());
	}

	@Override
	public List<GmEnumConstantInfo> getGmEnumConstantInfos() {
		return flatEnumConstant.infos;
	}

	@Override
	public Stream<MetaData> getMetaData() {
		return getGmEnumConstantInfos().stream().flatMap(gmEnumConstatntInfo -> nullSafe(gmEnumConstatntInfo.getMetaData()).stream());
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedMetaData() {
		return getGmEnumConstantInfos().stream().flatMap(QualifiedMetaDataTools::ownMetaData);
	}

}
