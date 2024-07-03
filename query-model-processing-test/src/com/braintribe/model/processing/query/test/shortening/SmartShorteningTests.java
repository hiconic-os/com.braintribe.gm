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
package com.braintribe.model.processing.query.test.shortening;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.query.shortening.SmartShortening;
import com.braintribe.model.processing.query.test.shortening.model.AmbiguiousName;
import com.braintribe.model.processing.query.test.shortening.model.ShorteningTestModel;

/**
 * @author peter.gazdik
 */
public class SmartShorteningTests {

	private final SmartShortening signatureExpert = new SmartShortening(getModelOracle());

	@Test
	public void ambiguousName() throws Exception {
		String s1 = signatureExpert.shorten(AmbiguiousName.T.getTypeSignature());
		String s2 = signatureExpert.shorten(com.braintribe.model.processing.query.test.shortening.model.sub.AmbiguiousName.T.getTypeSignature());

		assertThat(s1).isEqualTo("model.AmbiguiousName");
		assertThat(s2).isEqualTo("sub.AmbiguiousName");
	}
	
	@Test
	public void expandAmbiguousShortennedName() throws Exception {
		String s1 = signatureExpert.expand("model.AmbiguiousName");
		String s2 = signatureExpert.expand("sub.AmbiguiousName");
		
		assertThat(s1).isEqualTo("com.braintribe.model.processing.query.test.shortening.model.AmbiguiousName");
		assertThat(s2).isEqualTo("com.braintribe.model.processing.query.test.shortening.model.sub.AmbiguiousName");
	}

	public static ModelOracle getModelOracle() {
		return new BasicModelOracle(ShorteningTestModel.raw());
	}

}
