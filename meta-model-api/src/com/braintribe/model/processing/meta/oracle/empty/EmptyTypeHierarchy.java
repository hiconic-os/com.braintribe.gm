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
import java.util.Set;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.meta.oracle.EntityTypeOracle;
import com.braintribe.model.processing.meta.oracle.TypeHierarchy;

/**
 * @author peter.gazdik
 */
public class EmptyTypeHierarchy implements TypeHierarchy {

	public static final EmptyTypeHierarchy INSTANCE = new EmptyTypeHierarchy();

	private EmptyTypeHierarchy() {
	}

	@Deprecated
	@Override
	public TypeHierarchy transitive(boolean includeSelf) {
		return this;
	}

	@Override
	public TypeHierarchy transitive() {
		return this;
	}

	@Override
	public TypeHierarchy includeSelf() {
		return this;
	}

	@Override
	public TypeHierarchy includeSelfForce() {
		return this;
	}

	@Override
	public TypeHierarchy onlyInstantiable() {
		return this;
	}

	@Override
	public TypeHierarchy onlyAbstract() {
		return this;
	}

	@Override
	public TypeHierarchy includeBaseType() {
		return this;
	}

	@Override
	@Deprecated
	public TypeHierarchy includeSimpleTypes() {
		return this;
	}

	@Override
	public TypeHierarchy sorted(Order order) {
		return this;
	}

	@Override
	public <T extends GmType> Set<T> asGmTypes() {
		return Collections.emptySet();
	}

	@Override
	public <T extends GenericModelType> Set<T> asTypes() {
		return Collections.emptySet();
	}

	@Override
	public Set<EntityTypeOracle> asEntityTypeOracles() {
		return Collections.emptySet();
	}

}
