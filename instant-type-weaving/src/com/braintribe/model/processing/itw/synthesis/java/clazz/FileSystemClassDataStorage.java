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
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.braintribe.model.access.ClassDataStorage;
import com.braintribe.model.processing.itw.asm.AsmNewClass;
import com.braintribe.model.processing.itw.synthesis.java.JavaTypeSynthesisRuntimeException;
import com.braintribe.utils.FileTools;

/**
 * 
 */
public class FileSystemClassDataStorage implements ClassDataStorage {

	private final File folder;
	private final Set<String> existingClasses;
	private final Set<String> readOnly_ExistingClasses;

	public FileSystemClassDataStorage(File folder) {
		this.folder = folder;
		this.existingClasses = new HashSet<String>();
		this.readOnly_ExistingClasses = Collections.unmodifiableSet(existingClasses);

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

	private void registerFile(File f) {
		String className = f.getName();
		existingClasses.add(className);
	}

	@Override
	public void storeClass(String qualifiedName, InputStream inputStream, Set<String> dependencies) throws Exception {
		throw new UnsupportedOperationException("Method 'FileSystemClassDataStorage.storeClass' is not implemented yet!");
	}

	public void storeClass(AsmNewClass newClass) {
		FileTools.writeBytesToFile(new File(folder, newClass.getName()), newClass.getBytes());
	}

	@Override
	public Set<String> getQualifiedNamesOfStoredClasses() throws Exception {
		return readOnly_ExistingClasses;
	}

}
