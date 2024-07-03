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
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.processing.meta.oracle.ElementInOracleNotFoundException;
import com.braintribe.model.processing.meta.oracle.EnumConstantOracle;
import com.braintribe.model.processing.meta.oracle.EnumTypeConstants;
import com.braintribe.model.processing.meta.oracle.EnumTypeOracle;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;

/**
 * @author peter.gazdik
 */
public class EmptyEnumTypeOracle extends EmptyTypeOracle implements EnumTypeOracle {

	public EmptyEnumTypeOracle(ModelOracle modelOracle) {
		super(modelOracle);
	}

	protected final EmptyEnumConstantOracle emptyConstantOracle = new EmptyEnumConstantOracle(this);

	@Override
	public GmEnumType asGmEnumType() {
		return null;
	}

	@Override
	public List<GmEnumTypeInfo> getGmEnumTypeInfos() {
		return Collections.emptyList();
	}

	@Override
	public EnumTypeConstants getConstants() {
		return EmptyEnumTypeConstants.INSTANCE;
	}

	@Override
	public EnumConstantOracle getConstant(String constantName) {
		EnumConstantOracle result = findConstant(constantName);
		if (result == null) {
			throw new ElementInOracleNotFoundException("Constant not found: " + constantName);
		}

		return result;
	}

	@Override
	public EnumConstantOracle getConstant(GmEnumConstant constant) {
		return getConstant(constant.getName());
	}

	@Override
	public EnumConstantOracle getConstant(Enum<?> enumValue) {
		return getConstant(enumValue.name());
	}

	@Override
	public EnumConstantOracle findConstant(String constantName) {
		return emptyConstantOracle;
	}

	@Override
	public EnumConstantOracle findConstant(GmEnumConstant constant) {
		return findConstant(constant.getName());
	}

	@Override
	public EnumConstantOracle findConstant(Enum<?> enumValue) {
		return findConstant(enumValue.name());
	}

	@Override
	public Stream<MetaData> getConstantMetaData() {
		return Stream.empty();
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedConstantMetaData() {
		return Stream.empty();
	}

}
