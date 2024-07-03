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
package com.braintribe.model.processing.itw.asm;

/**
 * 
 */
public abstract class AsmLoadableClass extends AsmClass {

	byte[] bytes;
	Class<?> loadedClass;

	public AsmLoadableClass(String name, AsmClassPool classPool) {
		super(name, classPool);
	}

	/**
	 * JLS $12.7 says: A class or interface may be unloaded if and only if its defining class loader may be reclaimed by the garbage collector as
	 * discussed in $12.6
	 * 
	 * This means, once we load our class with our ClassLoader, we may delete the bytes as the class will be loaded at least as long as the
	 * ClassLoader -> there will never be any need to load it again with the same ClassLoader.
	 */
	public synchronized void notifyLoaded(Class<?> loadedClass) {
		this.bytes = null;
		this.loadedClass = loadedClass;
	}

	@Override
	public Class<?> getJavaType() {
		if (loadedClass == null)
			throw new IllegalStateException("Cannot get javaType, this class wasn't loaded yet: " + name);

		return loadedClass;
	}

	/** We need this to be public in case we want to do something with the bytes, like storing them in a file. */
	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public final boolean isPrimitive() {
		return false;
	}

	@Override
	public final String toString() {
		return getClass().getSimpleName() + "[" + name + "]";
	}

}
