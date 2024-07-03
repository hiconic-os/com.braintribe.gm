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
package com.braintribe.model.processing.vde.impl.bvd.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.bvd.navigation.PropertyPath;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.navigation.PropertyPathVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.impl.misc.Name;
import com.braintribe.model.processing.vde.impl.misc.Person;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link PropertyPathVde}.
 * 
 */
public class PropertyPathVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	/**
	 * Validate that a {@link PropertyPath}, which references a non-null property in a class, will evaluate to the
	 * correct property
	 */
	@Test
	public void testFullPropertyPath() throws Exception {
		// init test data
		final Name n = Name.T.create();
		n.setFirst("A");
		n.setMiddle("B");
		n.setLast("C");

		final Person p = Person.T.create();
		p.setName(n);

		final PropertyPath path = $.propertyPath();
		path.setPropertyPath("name.first");
		path.setEntity(p);
		
		// run the evaluate method
		Object result = evaluate(path);

		// validate output
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo("A");
	}

	/**
	 * Validate that a {@link PropertyPath}, which references a null property in a class, will evaluate to null
	 */
	@Test
	public void testEmptyPropertyPath() throws Exception {
		// init test data
		Name n = Name.T.create();
		n.setFirst("A");
		n.setLast("C");

		Person p = Person.T.create();
		p.setName(n);

		PropertyPath path = $.propertyPath(); 
		path.setPropertyPath("name.middle");
		path.setEntity(p);

		// run the evaluate method
		Object result = evaluate(path);

		// validate output
		assertThat(result).isNull();
	}
	

	@Test
	public void testNullEntityPropertyPath() throws Exception {

		PropertyPath path = $.propertyPath(); 
		path.setPropertyPath("name.middle");
		path.setEntity(null);

		// run the evaluate method
		Object result = evaluate(path);

		// validate output
		assertThat(result).isNull();
	}

}
