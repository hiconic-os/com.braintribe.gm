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
package com.braintribe.model.processing.classloading;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author peter.gazdik
 */
public class ParentLastUrlClassLoader extends URLClassLoader {

	private final ClassFilter classFilter;

	public ParentLastUrlClassLoader(URL[] urls, ClassLoader parent, ClassFilter classFilter) {
		super(urls, parent);
		this.classFilter = classFilter;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> result = findLoadedClass(name);
			if (result != null) {
				return result;
			}

			if (classFilter.forceLoadFromParentFirst(name)) {
				return super.loadClass(name, resolve);
			}

			result = tryLoadFromUrls(name);
			if (result != null) {
				return result;
			}

			return getParent().loadClass(name);
		}
	}

	private Class<?> tryLoadFromUrls(String name) {
		try {
			return findClass(name);

		} catch (ClassNotFoundException e) {
			return null;
		}
	}

}
