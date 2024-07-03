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
package com.braintribe.model.processing.meta.oracle.empty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.processing.meta.oracle.EnumConstantOracle;
import com.braintribe.model.processing.meta.oracle.EnumTypeOracle;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;

/**
 * @author peter.gazdik
 */
public class EmptyEnumConstantOracle implements EnumConstantOracle {

	private final EnumTypeOracle enumTypeOracle;

	public EmptyEnumConstantOracle(EnumTypeOracle enumTypeOracle) {
		this.enumTypeOracle = enumTypeOracle;
	}

	@Override
	public EnumTypeOracle getEnumTypeOracle() {
		return enumTypeOracle;
	}

	@Override
	public GmEnumConstant asGmEnumConstant() {
		return null;
	}

	@Override
	public Enum<?> asEnum() {
		return null;
	}

	@Override
	public List<GmEnumConstantInfo> getGmEnumConstantInfos() {
		return Collections.emptyList();
	}

	@Override
	public Stream<MetaData> getMetaData() {
		return Stream.empty();
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedMetaData() {
		return Stream.empty();
	}

}
