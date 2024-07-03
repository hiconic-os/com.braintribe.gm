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

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Not thread-safe!
 */
@SuppressWarnings({ "removal", "deprecation" })
public class AsmClassLoaderWrapper implements AsmClassLoading {

	private final Map<String, AsmLoadableClass> asmClasses = new HashMap<>();
	private final Map<String, Class<?>> loadedAsmClasses = new HashMap<>();
	private final ReentrantLock loadedAsmClassesLock = new ReentrantLock();

	private final ClassLoader contextClassLoader;
	private static Method defineClassMethod;

	static {
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
				@Override
				public Object run() throws Exception {
					Class<?> cl = Class.forName("java.lang.ClassLoader");
					defineClassMethod = cl.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class });
					defineClassMethod.setAccessible(true);
					return null;
				}
			});

		} catch (PrivilegedActionException pae) {
			throw new RuntimeException("cannot initialize ClassPool", pae.getException());
		}
	}

	public AsmClassLoaderWrapper() {
		contextClassLoader = AsmClassLoading.contextClassLoader();
	}

	@Override
	public ClassLoader getItwClassLoader() {
		return contextClassLoader;
	}

	@Override
	public void register(AsmLoadableClass asmClass) {
		asmClasses.put(asmClass.getName(), asmClass);
	}

	@Override
	public <T> Class<T> getJvmClass(String name) throws ClassNotFoundException {
		return (Class<T>) loadClass(name);
	}

	protected Class<?> loadClass(String name) throws ClassNotFoundException {
		if (loadedAsmClasses.containsKey(name))
			return loadedAsmClasses.get(name);

		loadedAsmClassesLock.lock();
		try {
			if (loadedAsmClasses.containsKey(name)) {
				return loadedAsmClasses.get(name);
			}

			if (asmClasses.containsKey(name)) {
				AsmLoadableClass asmClass = asmClasses.get(name);

				ensureLoaded(asmClass.getSuperClass());
				for (AsmClass iface : asmClass.getInterfaces())
					ensureLoaded(iface);

				return loadClass(asmClass);
			}

			return Class.forName(name);
		} finally {
			loadedAsmClassesLock.unlock();
		}
	}

	private void ensureLoaded(AsmClass asmClass) throws ClassNotFoundException {
		if (asmClass instanceof AsmNewClass)
			loadClass(asmClass.getName());
	}

	private Class<?> loadClass(AsmLoadableClass asmClass) {
		byte[] bytes = asmClass.getBytes();
		Class<?> result = defineClass(asmClass.getName(), bytes, 0, bytes.length);
		notifyClassLoaded(asmClass, result);
		return result;
	}

	private Class<?> defineClass(String name, byte[] bytes, int i, int length) {
		try {
			return (Class<?>) defineClassMethod.invoke(contextClassLoader, name, bytes, i, length);

		} catch (RuntimeException e) {
			throw e;

		} catch (Exception e) {
			throw new RuntimeException("Problem while loading class using system class-loader.", e);
		}
	}

	private void notifyClassLoaded(AsmLoadableClass asmClass, Class<?> loadedClass) {
		asmClass.notifyLoaded(loadedClass);

		asmClasses.remove(asmClass.getName());
		loadedAsmClasses.put(asmClass.getName(), loadedClass);
	}

}
