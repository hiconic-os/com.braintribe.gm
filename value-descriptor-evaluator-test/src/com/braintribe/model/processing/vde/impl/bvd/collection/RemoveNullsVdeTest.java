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
package com.braintribe.model.processing.vde.impl.bvd.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Test;

import com.braintribe.model.bvd.convert.collection.RemoveNulls;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.convert.ToBooleanVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * Provides tests for {@link ToBooleanVde}.
 * 
 */
public class RemoveNullsVdeTest extends VdeTest {
	
	public static VDGenerator $ = new VDGenerator(); 

	@Test
	public void testRemoveNullsFromList() throws Exception {

		List<?> collection = CollectionTools2.asList(1,2,3,null,4,5,null);
		RemoveNulls removeNulls = $.removeNulls();
		removeNulls.setCollection(collection);
		
		Object result = evaluate(removeNulls);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(List.class);
		List<?> resultList = (List<?>)result;
		assertThat(resultList.size()).isEqualTo(5);
		assertThat(resultList).doesNotContainNull();
		assertThat(resultList.containsAll(Arrays.asList(new Integer[] {1,2,3,4,5}))).isTrue();
	}

}
