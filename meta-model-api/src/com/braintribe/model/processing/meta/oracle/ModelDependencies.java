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

import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;

/**
 * 
 */
public interface ModelDependencies {

	/** @deprecated use {@link #transitive()} and {@link #includeSelf()} */
	@Deprecated
	ModelDependencies transitive(boolean includeSelf);

	ModelDependencies transitive();

	ModelDependencies includeSelf();

	/**
	 * TODO this might be dangerous, the model equivalent exists only sometimes, and I think only in JVM?
	 */
	Stream<Model> asModels();

	Stream<GmMetaModel> asGmMetaModels();

	Stream<ModelOracle> asModelOracles();

}
