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

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.processing.meta.oracle.EnumConstantOracle;
import com.braintribe.model.processing.meta.oracle.EnumTypeConstants;

/**
 * @author peter.gazdik
 */
public class EmptyEnumTypeConstants implements EnumTypeConstants {

	public static final EmptyEnumTypeConstants INSTANCE = new EmptyEnumTypeConstants();

	private EmptyEnumTypeConstants() {
	}

	@Override
	public EnumTypeConstants filter(Predicate<? super GmEnumConstant> filter) {
		return this;
	}

	@Override
	public Stream<GmEnumConstant> asGmEnumConstants() {
		return Stream.empty();
	}

	@Override
	public Stream<Enum<?>> asEnums() {
		return Stream.empty();
	}

	@Override
	public Stream<EnumConstantOracle> asEnumConstantOracles() {
		return Stream.empty();
	}

}
