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
package com.braintribe.model.processing.vde.impl.bvd.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.Test;

import com.braintribe.model.bvd.context.CurrentLocale;
import com.braintribe.model.processing.vde.evaluator.api.aspects.CurrentLocaleAspect;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;

public class CurrentLocaleVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator();

	@Test
	public void testCurrentLocale() throws Exception {
		CurrentLocale currentLocale = $.currentLocale();

		Object result = evaluateWith(CurrentLocaleAspect.class, () -> Locale.TAIWAN.toString(), currentLocale);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(String.class);
		assertThat(result).isEqualTo(Locale.TAIWAN.toString());
	}

}
