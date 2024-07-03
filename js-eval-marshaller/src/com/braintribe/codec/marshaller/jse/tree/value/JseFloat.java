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
package com.braintribe.codec.marshaller.jse.tree.value;

import java.io.IOException;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.jse.CountingWriter;
import com.braintribe.codec.marshaller.jse.tree.JseNode;

public class JseFloat extends JseNode {
	private static final char[] functionStart = "$.f(".toCharArray();
	
	private Object value;
	
	public JseFloat(Object value) {
		super();
		this.value = value;
	}

	@Override
	public void write(CountingWriter writer) throws MarshallException, IOException {
		writer.write(functionStart);
		writer.write(value.toString());
		writer.write(')');
	}
}
