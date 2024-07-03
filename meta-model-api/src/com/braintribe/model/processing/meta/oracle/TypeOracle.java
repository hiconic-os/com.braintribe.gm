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

import com.braintribe.model.generic.reflection.CustomType;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.meta.data.MetaData;

/**
 * @author peter.gazdik
 */
public interface TypeOracle {

	/** Returns the {@link ModelOracle} from which this {@link TypeOracle} was returned. */
	ModelOracle getModelOracle();

	<T extends GmCustomType> T asGmType();

	<T extends CustomType> T asType();

	/**
	 * @returns all metaData defined for given {@link CustomType}, including overrides - iteration over models is done
	 *          in depth first order.
	 */
	Stream<MetaData> getMetaData();

	/** Qualified version of {@link #getMetaData()}. */
	Stream<QualifiedMetaData> getQualifiedMetaData();

	/**
	 * @return true iff custom type behind this oracle is declared in the model corresponding to the {@link ModelOracle}
	 *         from which our oracle was returned.
	 */
	boolean isDeclared();

}
