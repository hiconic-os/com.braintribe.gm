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

public class JseLong extends JseNode {
	protected static final int BITS = 22;
	protected static final int BITS01 = 2 * BITS;
	protected static final int BITS2 = 64 - BITS01;
	protected static final int MASK = (1 << BITS) - 1;
	protected static final int MASK_2 = (1 << BITS2) - 1;
	
	protected static final char[]  envelope1 = "$.l({l:".toCharArray();
	protected static final char[]  envelope2 = ",m:".toCharArray();
	protected static final char[]  envelope3 = ",h:".toCharArray();
	protected static final char[]  envelope4 = "})".toCharArray();
	private long value;

	public JseLong(long value) {
		super();
		this.value = value;
	}
	
	@Override
	public void write(CountingWriter writer) throws MarshallException, IOException {
		/**
		 * Return a triple of ints { low, middle, high } that concatenate
		 * bitwise to the given number.
		 */
		long l = value;
		writer.write(envelope1);
		writer.write(String.valueOf((int) (l & MASK)));
		writer.write(envelope2);
		writer.write(String.valueOf((int) ((l >> BITS) & MASK)));
		writer.write(envelope3);
		writer.write(String.valueOf((int) ((l >> BITS01) & MASK_2)));
		writer.write(envelope4);
	}
}
