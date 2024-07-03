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

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.processing.meta.oracle.EntityTypeProperties;
import com.braintribe.model.processing.meta.oracle.PropertyOracle;

/**
 * @author peter.gazdik
 */
public class EmptyEntityTypeProperties implements EntityTypeProperties {

	public static final EmptyEntityTypeProperties INSTANCE = new EmptyEntityTypeProperties();

	private EmptyEntityTypeProperties() {
	}

	@Override
	public EntityTypeProperties filter(Predicate<? super GmProperty> filter) {
		return this;
	}

	@Override
	public EntityTypeProperties onlyDeclared() {
		return this;
	}

	@Override
	public EntityTypeProperties onlyInherited() {
		return this;
	}

	@Override
	public Stream<GmProperty> asGmProperties() {
		return Stream.empty();
	}

	@Override
	public Stream<Property> asProperties() {
		return Stream.empty();
	}

	@Override
	public Stream<PropertyOracle> asPropertyOracles() {
		return Stream.empty();
	}

}
