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
package com.braintribe.model.processing.oracle;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.meta.oracle.BasicEntityTypeOracle;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.meta.oracle.EntityTypeOracle;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.oracle.model.ModelOracleModelProvider;
import com.braintribe.model.processing.oracle.model.basic.mammal.Dog;
import com.braintribe.model.processing.oracle.model.basic.mammal.Mammal;
import com.braintribe.model.processing.oracle.model.evaluable.GenericEntityEvaluable;
import com.braintribe.model.processing.oracle.model.evaluable.GenericEntityEvaluable2;
import com.braintribe.model.processing.oracle.model.evaluable.MammalEvaluable;
import com.braintribe.model.processing.oracle.model.evaluable.MammalPetEvaluable;
import com.braintribe.model.processing.oracle.model.evaluable.ObjectEvaluable;
import com.braintribe.model.processing.oracle.model.evaluable.StringEvaluable;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see EntityTypeOracle
 * @see BasicEntityTypeOracle
 * 
 * @author peter.gazdik
 */
public class BasicEntityTypeOracle_Eval_Test {

	protected static GmMetaModel evalModel = ModelOracleModelProvider.evalModel();
	protected static ModelOracle oracle = new BasicModelOracle(evalModel);

	@Test
	public void checkBasicEvaluables() throws Exception {
		assertEvaluatesTo(GenericEntity.T, (GmType) null);

		assertEvaluatesTo(ServiceRequest.T, oracle.getGmBaseType());
		assertEvaluatesTo(ObjectEvaluable.T, oracle.getGmBaseType());
		assertEvaluatesTo(StringEvaluable.T, oracle.getGmStringType());
		
		assertEvaluatesTo(GenericEntityEvaluable.T, GenericEntity.T);
		assertEvaluatesTo(GenericEntityEvaluable2.T, GenericEntity.T);
		assertEvaluatesTo(MammalEvaluable.T, Mammal.T);
		assertEvaluatesTo(MammalPetEvaluable.T, Dog.T);
	}

	private void assertEvaluatesTo(EntityType<?> et, EntityType<?> evaluatesToEt) {
		assertEvaluatesTo(et, oracle.getEntityTypeOracle(evaluatesToEt).asGmEntityType());
	}

	private void assertEvaluatesTo(EntityType<?> et, GmType expectedType) {
		GmType evaluatesTo = getEvaluatesTo(et);
		Assertions.assertThat(evaluatesTo).isSameAs(expectedType);
	}
	
	private GmType getEvaluatesTo(EntityType<?> et) {
		return oracle.getEntityTypeOracle(et).getEvaluatesTo().orElse(null);
	}

	protected static EntityTypeOracle getEntityOracle(EntityType<?> et) {
		return oracle.getEntityTypeOracle(et);
	}

}
