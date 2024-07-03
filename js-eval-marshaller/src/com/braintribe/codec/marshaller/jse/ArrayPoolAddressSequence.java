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
package com.braintribe.codec.marshaller.jse;


public class ArrayPoolAddressSequence extends PoolAddressSequence {
	public int count = 0;
	private char[] name;
	
	public ArrayPoolAddressSequence(char[] name) {
		this.name = name;
	}
	
	public char[] next() {
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append('[');
		builder.append(count++);
		builder.append(']');
		int length = builder.length();
		char[] chars = new char[length];
		builder.getChars(0, length, chars, 0);
		return chars;
	}
	
	@Override
	public int getCount() {
		return count;
	}
}
