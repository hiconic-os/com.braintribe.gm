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

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.CustomType;

/**
 * @author peter.gazdik
 */
public abstract class BasicTypeOracle implements TypeOracle {

	protected final BasicModelOracle modelOracle;

	public BasicTypeOracle(BasicModelOracle modelOracle) {
		this.modelOracle = modelOracle;
	}

	@Override
	public ModelOracle getModelOracle() {
		return modelOracle;
	}

	@Override
	public final <T extends CustomType> T asType() {
		return GMF.getTypeReflection().<T> getType(asGmType().getTypeSignature());
	}

	@Override
	public boolean isDeclared() {
		return modelOracle.flatModel.model == asGmType().getDeclaringModel();
	}

}
