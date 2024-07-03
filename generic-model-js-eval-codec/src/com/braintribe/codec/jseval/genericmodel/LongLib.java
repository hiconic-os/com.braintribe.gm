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
package com.braintribe.codec.jseval.genericmodel;

public class LongLib {
	protected static final int BITS = 22;
	protected static final int BITS01 = 2 * BITS;
	protected static final int BITS2 = 64 - BITS01;
	protected static final int MASK = (1 << BITS) - 1;
	protected static final int MASK_2 = (1 << BITS2) - 1;

	/**
	 * Return a triple of ints { low, middle, high } that concatenate
	 * bitwise to the given number.
	 */
	public static int[] getAsIntArray(long l) {
		int[] a = new int[3];
		a[0] = (int) (l & MASK);
		a[1] = (int) ((l >> BITS) & MASK);
		a[2] = (int) ((l >> BITS01) & MASK_2);
		return a;
	}
}
