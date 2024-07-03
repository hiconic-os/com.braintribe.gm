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
package com.braintribe.model.processing.vde.impl.root;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.vde.evaluator.api.aspects.VariableProviderAspect;
import com.braintribe.model.processing.vde.evaluator.impl.root.VariableVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.impl.misc.VariableCustomProvider;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link VariableVde}.
 *
 */
public class VariableVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	/**
	 *  Validate that a {@link Variable} which has information that is available in a {@link VariableCustomProvider}, will evaluate properly.
	 */
	@Test
	public void testProviderWithValidData() throws Exception {

		// init object that will be tested
		VariableVde vde = new VariableVde();
		Variable vd = $.variable(); 
		vd.setName("key");
		VariableCustomProvider provider = new VariableCustomProvider();
		provider.add("key", "value");


		//validate input
		assertThat(provider).isNotNull();
		assertThat(vde).isNotNull();
		assertThat(vd).isNotNull();
		assertThat(vd.getName()).isEqualTo("key");
		
		// run the evaluate method
		Object result =  evaluateWith(VariableProviderAspect.class, provider, vd);

		//validate output
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(String.class);
		assertThat(result).isEqualTo("value");
	}

	/**
	 *  Validate that a {@link Variable} which has information that is NOT available in a {@link VariableCustomProvider}, will evaluate to null.
	 */
	@Test
	public void testProviderWithInValidData() throws Exception {

		// init object that will be tested
		VariableVde vde = new VariableVde();

		Variable vd = $.variable(); 
		vd.setName("otherKey");
		VariableCustomProvider provider = new VariableCustomProvider();
		provider.add("key", "value");


		// validate input
		assertThat(provider).isNotNull();
		assertThat(vde).isNotNull();
		assertThat(vd).isNotNull();
		assertThat(vd.getName()).isEqualTo("otherKey");

		// run the evaluate method
		Object result = evaluateWith(VariableProviderAspect.class, provider, vd);

		//validate output
		assertThat(result).isNull();
	}

}
