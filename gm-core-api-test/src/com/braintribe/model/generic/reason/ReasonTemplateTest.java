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
package com.braintribe.model.generic.reason;

import org.assertj.core.api.Assertions;

import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.model.generic.reason.model.TestReason;
import com.braintribe.model.generic.reason.model.TestSubject;

public class ReasonTemplateTest {

	//@Test
	public void testTemplateReason() throws Exception {
		TestSubject subject = TestSubject.T.create();
		subject.setName("Obelix");
		
		TestReason reason = Reasons.build(TestReason.T).enrich(r -> r.setSubject(subject)).toReason();
		
		Assertions.assertThat(reason.getText()).isEqualTo("You've got problems with subject Obelix");
	}
}
