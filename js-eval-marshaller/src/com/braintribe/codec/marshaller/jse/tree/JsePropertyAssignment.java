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
package com.braintribe.codec.marshaller.jse.tree;

import java.io.IOException;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.jse.CountingWriter;

public class JsePropertyAssignment extends JseNode {
	private static final char[] functionStart = "$.s(_,".toCharArray();
	private static final char[] statementEnd = ");".toCharArray();
	
	private JseNode property;
	private JseNode value;
	
	public JsePropertyAssignment(JseNode property, JseNode value) {
		super();
		this.property = property;
		this.value = value;
	}

	@Override
	public void write(CountingWriter writer) throws MarshallException, IOException {
		writer.write(functionStart);
		property.write(writer);
		writer.write(',');
		value.write(writer);
		writer.write(statementEnd);
	}
}
