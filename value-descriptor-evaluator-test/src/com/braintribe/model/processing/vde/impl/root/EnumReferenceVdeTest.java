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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.root.EnumReferenceVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.impl.misc.SalaryRange;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link EnumReferenceVde}.
 *
 */
public class EnumReferenceVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	@Test
	public void testEnumReference() throws Exception{
		EnumReference enumRef = $.enumReference();
		enumRef.setTypeSignature(SalaryRange.class.getName());
		enumRef.setConstant("medium");
		
		Object result = evaluate(enumRef);
		
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Enum.class);
		assertThat(result).isInstanceOf(SalaryRange.class);
		assertThat(result).isEqualTo(SalaryRange.medium);
	}
	
	@Test (expected = VdeRuntimeException.class)
	public void testEnumReferenceFail() throws Exception{
		EnumReference enumRef = $.enumReference();
		enumRef.setTypeSignature(SalaryRange.class.getName());
		enumRef.setConstant("xyz");
		
		evaluate(enumRef);
	}

}
