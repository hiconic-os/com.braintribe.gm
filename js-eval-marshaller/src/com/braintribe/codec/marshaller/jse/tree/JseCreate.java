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

public class JseCreate extends JseNode {

	private static final char[] functionStart = "=$.C(".toCharArray();
	private static final char[] functionEnd = ");".toCharArray();
	
	private JsePoolAddress address;
	private JseNode argument;
	
	public JseCreate(JsePoolAddress address, JseNode argument) {
		super();
		this.address = address;
		this.argument = argument;
	}

	@Override
	public void write(CountingWriter writer) throws MarshallException, IOException {
		address.write(writer);
		writer.write(functionStart);
		argument.write(writer);
		writer.write(functionEnd);
	}
}
