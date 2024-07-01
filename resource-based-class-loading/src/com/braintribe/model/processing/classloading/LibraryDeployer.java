// ============================================================================
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
package com.braintribe.model.processing.classloading;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;

/**
 * @author peter.gazdik
 */
public class LibraryDeployer extends AbstractLibraryDeployer {

	/**
	 * Deploys library, which is given by an index file - a text file containing a list of load-able jars, with one entry per line.
	 * 
	 * @param resourceAwareClassLoader
	 *            {@link ClassLoader} which is able to load given resource (zip file containing jars), BUT ALSO the parent for the returned
	 *            ClassLoader. We assume we can use the same ClassLoade for both. The parent is important as the one to load all the classes
	 *            that we might have accessible in the context which invokes this method (e.g. GenericEntity).
	 * @param libFolder
	 *            folder to contain the jars
	 *
	 * @param libIndexFileName
	 *            name of the index file which can be loaded with our {@link ClassLoader}
	 * 
	 * @return instance of {@link DeployedLibrary} with a {@link URLClassLoader} for given jars
	 * 
	 * @throws LibraryDeploymentException
	 */
	public static ClassLoader deployLibrary(ClassLoader resourceAwareClassLoader, File libFolder, String libIndexFileName,
			ClassFilter classFilter) throws LibraryDeploymentException {

		return new LibraryDeployer(resourceAwareClassLoader, libFolder, libIndexFileName, classFilter).deploy();
	}

	public LibraryDeployer(ClassLoader resourceAwareClassLoader, File libFolder, String libIndexFileName, ClassFilter classFilter) {
		super(resourceAwareClassLoader, libFolder, libIndexFileName, classFilter);
	}

	@Override
	protected void deploy(File outputFile, String libFileName) throws LibraryDeploymentException {
		try (InputStream is = resourceAwareClassLoader.getResourceAsStream(libFileName)) {
			if (is == null) {
				throw new LibraryDeploymentException("Library file not found as a resource: " + libFileName);
			}
			
			writeMissingLibFile(is, outputFile);

		} catch (IOException e) {
			throw new LibraryDeploymentException("Error whhile getting resource as stream. Resource name: " + libIndexFileName, e);
		}
	}

}
