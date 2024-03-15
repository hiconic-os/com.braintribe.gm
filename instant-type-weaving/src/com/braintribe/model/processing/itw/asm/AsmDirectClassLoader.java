// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.itw.asm;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Registering new {@link AsmLoadableClass loadable classes} is not thread-safe!
 */
public class AsmDirectClassLoader extends ClassLoader implements AsmClassLoading {

	private final Map<String, AsmLoadableClass> asmClasses = newMap();
	private final ReentrantLock lock = new ReentrantLock();

	public AsmDirectClassLoader() {
		super(AsmDirectClassLoader.class.getClassLoader());
	}

	@Override
	public ClassLoader getItwClassLoader() {
		return this;
	}

	@Override
	public void register(AsmLoadableClass asmClass) {
		asmClasses.put(asmClass.getName(), asmClass);
	}

	@Override
	public <T> Class<T> getJvmClass(String name) throws ClassNotFoundException {
		return (Class<T>) loadClass(name);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (asmClasses.containsKey(name)) {
			lock.lock();
			try {
				if (asmClasses.containsKey(name)) {
					return loadClass(asmClasses.get(name));
				}
			} finally {
				lock.unlock();
			}
		}
		return super.loadClass(name, resolve);
	}

	private Class<?> loadClass(AsmLoadableClass asmClass) {
		byte[] bytes = asmClass.bytes;
		Class<?> result = defineClass(asmClass.getName(), bytes, 0, bytes.length);
		notifyClassLoaded(asmClass, result);
		return result;
	}

	private void notifyClassLoaded(AsmLoadableClass asmClass, Class<?> result) {
		asmClass.notifyLoaded(result);

		asmClasses.remove(asmClass.getName());
	}

}
