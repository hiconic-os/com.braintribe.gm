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

import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.oracle.ModelDependencies;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

/**
 * @author peter.gazdik
 */
public class EmptyModelDependencies implements ModelDependencies {

	public static final EmptyModelDependencies INSTANCE = new EmptyModelDependencies();

	private EmptyModelDependencies() {
	}

	@Override
	@Deprecated
	public ModelDependencies transitive(boolean includeSelf) {
		return this;
	}

	@Override
	public ModelDependencies transitive() {
		return this;
	}

	@Override
	public ModelDependencies includeSelf() {
		return this;
	}

	@Override
	public Stream<Model> asModels() {
		return Stream.empty();
	}

	@Override
	public Stream<GmMetaModel> asGmMetaModels() {
		return Stream.empty();
	}

	@Override
	public Stream<ModelOracle> asModelOracles() {
		return Stream.empty();
	}

}
