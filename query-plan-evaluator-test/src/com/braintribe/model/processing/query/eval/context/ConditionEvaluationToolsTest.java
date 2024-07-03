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
package com.braintribe.model.processing.query.eval.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.AbstractBooleanAssert;
import org.junit.Test;

public class ConditionEvaluationToolsTest {

	private String pattern;

	@Test
	public void constant() throws Exception {
		pattern = "text";

		yes("text");
		no("atext");
	}

	@Test
	public void wildcardEnding() throws Exception {
		pattern = "text*";

		yes("text");
		yes("text.");
		yes("texta");
		yes("text*");
		no("atext");
	}

	@Test
	public void wildcardBeginning() throws Exception {
		pattern = "*text";

		yes("text");
		yes(".text");
		yes("atext");
		yes("*text");
		no("texta");
	}

	@Test
	public void singleCharWildcard() throws Exception {
		pattern = "t?xt";

		yes("text");
		yes("toxt");
		no("txt");
	}

	@Test
	public void escapedWildcard() throws Exception {
		pattern = "text\\*";

		yes("text*");
		no("text");
	}

	@Test
	public void escapedSingleCharWildcard() throws Exception {
		pattern = "text\\?";

		yes("text?");
		no("text.");
	}

	@Test
	public void specialPatternChar() throws Exception {
		pattern = "text[a-z]";

		yes("text[a-z]");
		no("texta");
	}

	@Test
	public void specialSqlChar2() throws Exception {
		pattern = "text_";

		yes("text_");
		no("text");
		no("text.");
	}

	private void yes(String s) {
		assertLike(s).isTrue();
	}

	private void no(String s) {
		assertLike(s).isFalse();
	}

	private AbstractBooleanAssert<?> assertLike(String s) {
		return assertThat(s.matches(ConditionEvaluationTools.convertToRegexPattern(pattern))).as("Wrong value for: " + s);
	}

}
