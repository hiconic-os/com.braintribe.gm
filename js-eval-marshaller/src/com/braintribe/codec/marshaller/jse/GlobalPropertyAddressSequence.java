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

public class GlobalPropertyAddressSequence extends PoolAddressSequence  {
	private static final char[] firstDigitChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static final char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$0123456789".toCharArray();
	
	private int[] sequence = new int[6];
	private int digits = 1;
	public int count = 0;
	
	public char[] next() {
		count++;
		char extract[] = new char[digits];
		char chars[] = firstDigitChars;
		int base = chars.length;
		boolean carry = true;
		for (int i = 0; i < digits; i++) {
			int d = sequence[i];
			
			extract[i] = chars[d];
			
			if (carry) {
				d++;
				
				if (d == base) {
					sequence[i] = 0;
				}
				else {
					sequence[i] = d;
					carry = false;
				}
			}
			
			if (i == 0) {
				chars = GlobalPropertyAddressSequence.chars;
				base = chars.length;
			}
		}
		
		if (carry) {
			digits++;
		}
		
		return extract;
	}
	
	public int getCount() {
		return count;
	}
	
	public static void main(String[] args) {
		GlobalPropertyAddressSequence sequence = new GlobalPropertyAddressSequence();
		for (int i = 0; i < 100000; i++) {
			System.out.println(new String(sequence.next()));
		}
	}
}
