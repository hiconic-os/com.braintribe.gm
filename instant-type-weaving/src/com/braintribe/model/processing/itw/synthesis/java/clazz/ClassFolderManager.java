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
package com.braintribe.model.processing.itw.synthesis.java.clazz;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.processing.itw.asm.AsmNewClass;
import com.braintribe.model.processing.itw.synthesis.java.JavaTypeSynthesisRuntimeException;
import com.braintribe.model.processing.itw.tools.ItwTools;
import com.braintribe.utils.FileTools;

/**
 * @deprecated should not have any usages, {@link ClassStorageManager} should be used instead;
 */
@Deprecated
public class ClassFolderManager {

	private final File folder;
	/* TODO DOCUMENT only classes from the beginning, this is not updated once more classes are created, no reason to do
	 * that */
	private final Set<String> existingClasses;
	private final Map<String, Class<?>> loadedClasses;

	public ClassFolderManager(String folderName) {
		this(new File(folderName));
	}

	public ClassFolderManager(File folder) {
		this.folder = folder;
		this.existingClasses = new HashSet<String>();
		this.loadedClasses = new HashMap<String, Class<?>>();

		ensureFolder(folder);
		indexFiles();
	}

	private static File ensureFolder(File folder) {
		try {
			return FileTools.ensureDirectoryExists(folder);

		} catch (Exception e) {
			throw new JavaTypeSynthesisRuntimeException("Failed to configure class output folder as: " + folder.getName(), e);
		}
	}

	private void indexFiles() {
		if (!folder.isDirectory()) {
			return;
		}

		for (File f: folder.listFiles()) {
			if (!f.isDirectory()) {
				registerFile(f);
			}
		}
	}

	public Set<String> getExistingClasses() {
		return Collections.unmodifiableSet(existingClasses);
	}

	private Class<?> getClassIfExists(String className) {
		return ItwTools.findClass(className);
	}

	public void onClassCreated(AsmNewClass result) {
		FileTools.writeBytesToFile(new File(folder, result.getName()), result.getBytes());
	}

	private void registerFile(File f) {
		String className = f.getName();
		existingClasses.add(className);

		Class<?> clazz = getClassIfExists(className);
		if (clazz != null) {
			loadedClasses.put(className, clazz);
		}
	}

	public boolean containsLoadedClass(String className) {
		return loadedClasses.containsKey(className);
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getLoadedClass(String className) {
		return (Class<T>) loadedClasses.get(className);
	}
}
