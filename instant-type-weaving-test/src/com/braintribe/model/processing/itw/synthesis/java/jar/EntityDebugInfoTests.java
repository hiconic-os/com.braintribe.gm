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
package com.braintribe.model.processing.itw.synthesis.java.jar;

import org.junit.Test;

import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * 
 */
public class EntityDebugInfoTests extends ImportantItwTestSuperType {

	StringBuilder sb = new StringBuilder();

	@Test
	public void checkWorks() {
		addLine("class X {");
		addLine("");
		addLine("  public String getValue(){");
		addLine("    return null;");
		addLine("  }");
		addLine("");
		addLine("  public void setValue(String otherValue ) {");
		addLine("    this.value = otherValue;");
		addLine("  }");
		addLine("");
		addLine("}");

		EntityDebugInfo info = new EntityDebugInfo(sb.toString());

		BtAssertions.assertThat(info.getMethodLine("getValue")).isEqualTo(4);
		BtAssertions.assertThat(info.getMethodLine("setValue")).isEqualTo(8);
		BtAssertions.assertThat(info.getSetterParameterName("setValue")).isEqualTo("otherValue");

	}

	private void addLine(String s) {
		sb.append(s);
		sb.append("\n");
	}

}
