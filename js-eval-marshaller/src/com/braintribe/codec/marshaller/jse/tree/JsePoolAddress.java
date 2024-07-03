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
import com.braintribe.codec.marshaller.jse.PoolAddressSequence;

public class JsePoolAddress extends JseNode {
	private char address[];
	private PoolAddressSequence sequence;
	

	public JsePoolAddress(PoolAddressSequence sequence) {
		super();
		this.sequence = sequence;
	}
	
	@Override
	public void write(CountingWriter writer) throws MarshallException, IOException {
		if (address == null) {
			address = sequence.next();
		}
		writer.write(address);
	}
	
	public char[] getAddress() {
		if (address == null) {
			address = sequence.next();
		}
		return address;
	}
}
