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
package com.braintribe.model.processing.vde.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.vde.parser.TemplateStringParser;

public class TemplateStringParserTest {
	@Test 
	public void testDollarNonBracketNonName() {
		String subjects[] = {
				"$(name)",
				"$[name]",
				"$!"};

		for (String subject: subjects) {
			Object result = TemplateStringParser.parse(subject);
			assertThat(result).isEqualTo(subject);
		}
	}
	
	@Test
	public void testTemplateParsing() {
		Object o1 = TemplateStringParser.parse("");
		Object o2 = TemplateStringParser.parse("Hallo $name");
		Object o3 = TemplateStringParser.parse("$name");
		Object o4 = TemplateStringParser.parse("Just text");
		Object o5 = TemplateStringParser.parse("Escape $${ Test");
		
		assertThat(o1).isEqualTo("");
		assertThat(o2).isInstanceOf(Concatenation.class);
		assertThat(o3).isInstanceOf(Variable.class);
		assertThat(o4).isEqualTo("Just text");
		assertThat(o5).isEqualTo("Escape ${ Test");
		
		Concatenation c = (Concatenation)o2;
		
		List<Object> operands = c.getOperands();
		assertThat(operands.size()).isEqualTo(2);
		
		assertThat(operands.get(0)).isInstanceOf(String.class);
		assertThat(operands.get(1)).isInstanceOf(Variable.class);
		
		
	}
	
	@Test(expected = IllegalStateException.class)
	public void testTemplateParsingUnfinishedBracedVar() {
		TemplateStringParser.parse("Hallo${egal");
	}
	
	@Test(expected = IllegalStateException.class)
	public void testTemplateParsingIncompleteVarAtEnd() {
		TemplateStringParser.parse("Hallo$");
	}
}
