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

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.meta.GmEnumConstant;

/**
 * @author peter.gazdik
 */
public class BasicEnumTypeConstants implements EnumTypeConstants {

	private final BasicEnumTypeOracle enumTypeOracle;

	private Predicate<? super GmEnumConstant> filter;

	public BasicEnumTypeConstants(BasicEnumTypeOracle enumTypeOracle) {
		this.enumTypeOracle = enumTypeOracle;
	}

	@Override
	public EnumTypeConstants filter(Predicate<? super GmEnumConstant> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public Stream<Enum<?>> asEnums() {
		EnumType<?> et = BasicModelOracle.typeReflection.getType(enumTypeOracle.flatEnumType.type.getTypeSignature());
		return asGmEnumConstants().map(gmConstant -> et.getEnumValue(gmConstant.getName()));
	}

	@Override
	public Stream<EnumConstantOracle> asEnumConstantOracles() {
		return asGmEnumConstants().map(gmConstant -> enumTypeOracle.getConstant(gmConstant));
	}

	@Override
	public Stream<GmEnumConstant> asGmEnumConstants() {
		Stream<GmEnumConstant> result = getDeclaredConstants();

		if (filter != null) {
			result = result.filter(filter);
		}

		return result;
	}

	private Stream<GmEnumConstant> getDeclaredConstants() {
		return nullSafe(enumTypeOracle.flatEnumType.type.getConstants()).stream();
	}

}
