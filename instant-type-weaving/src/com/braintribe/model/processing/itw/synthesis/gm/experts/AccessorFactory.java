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
package com.braintribe.model.processing.itw.synthesis.gm.experts;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author peter.gazdik
 */
public class AccessorFactory {

	private static final Lookup lookup = MethodHandles.lookup();

	public static <B, T> Function<B, T> getter(Class<?> ownerType, String getterName, Class<?> propertyType) {
		try {
			CallSite site = LambdaMetafactory.metafactory(lookup, //
					"apply", //
					MethodType.methodType(Function.class), //
					MethodType.methodType(Object.class, Object.class), //
					lookup.findVirtual(ownerType, getterName, MethodType.methodType(propertyType)), //
					MethodType.methodType(propertyType, ownerType) //
			);

			MethodHandle factory = site.getTarget();
			Function<B, T> getter = (Function<B, T>) factory.invokeExact();

			return getter;

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <B, T> BiConsumer<B, T> setter(Class<?> ownerType, String setterName, Class<?> propertyType) {
		try {
			CallSite site = LambdaMetafactory.metafactory(lookup, //
					"accept", //
					MethodType.methodType(BiConsumer.class), //
					MethodType.methodType(void.class, Object.class, Object.class), //
					lookup.findVirtual(ownerType, setterName, MethodType.methodType(void.class, propertyType)), //
					MethodType.methodType(void.class, ownerType, propertyType) //
			);

			MethodHandle factory = site.getTarget();
			BiConsumer<B, T> setter = (BiConsumer<B, T>) factory.invokeExact();

			return setter;

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
