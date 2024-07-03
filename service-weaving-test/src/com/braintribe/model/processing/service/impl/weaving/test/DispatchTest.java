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
package com.braintribe.model.processing.service.impl.weaving.test;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.processing.service.api.UnsupportedRequestTypeException;
import com.braintribe.model.processing.service.impl.weaving.test.model.SubRequest1;
import com.braintribe.model.processing.service.impl.weaving.test.model.SubRequest2;
import com.braintribe.model.processing.service.impl.weaving.test.model.SubRequest3;
import com.braintribe.model.processing.service.impl.weaving.test.model.SubSubRequest2;


public class DispatchTest {
	@Test
	public void testDirect() throws Exception {
		TestServiceProcessor processor = new TestServiceProcessor();
		String result1 = processor.process(null, SubRequest1.T.create());
		String result2 = processor.process(null, SubRequest2.T.create());
		
		assertThat(result1).isEqualTo("subRequest1");
		assertThat(result2).isEqualTo("subRequest2");
	}
	
	@Test
	public void testPolymorphic() throws Exception {
		TestServiceProcessor processor = new TestServiceProcessor();
		String result = processor.process(null, SubSubRequest2.T.create());
		
		assertThat(result).isEqualTo("subRequest2");
	}
	
	@Test(expected=UnsupportedRequestTypeException.class)
	public void testUnmapped() throws Exception {
		TestServiceProcessor processor = new TestServiceProcessor();
		processor.process(null, SubRequest3.T.create());
	}
}
