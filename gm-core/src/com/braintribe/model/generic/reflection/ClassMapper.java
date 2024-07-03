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
package com.braintribe.model.generic.reflection;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ClassMapper {

	// @formatter:off
	static Class<?>[] classes = {
			String.class,
			Boolean.class,
			Integer.class,
			Long.class,
			Float.class,
			Double.class,
			BigDecimal.class,
			Date.class,
			java.sql.Time.class,
			java.sql.Date.class,
			java.sql.Timestamp.class,
			java.util.ArrayList.class,
			java.util.LinkedList.class,
			java.util.HashSet.class,
			java.util.LinkedHashSet.class,
			java.util.TreeSet.class,
			java.util.HashMap.class,
			java.util.LinkedHashMap.class,
			java.util.IdentityHashMap.class,
			java.util.TreeMap.class
	};
	// @formatter:on
	 
	public static void main(String[] args) {
		Set<Integer> hashes = new HashSet<>();

		for (Class<?> clazz : classes) {
			String name = clazz.getSimpleName();
			String fullName = clazz.getName();
//			int hash = name.charAt(0) ^ name.charAt(name.length() - 1);
			int hash = count(name.charAt(0), name.charAt(name.length() - 1), fullName.length());
			hashes.add(hash);
			System.out.println(name + " -> " + hash);
		}

		System.out.println(classes.length + " : " + hashes.size());
		System.out.println(hashes);

	}

	private static int count(int a, int b, int c) {
//		return (b + c) ^ a;
		return a ^ b + c;
	}

}
