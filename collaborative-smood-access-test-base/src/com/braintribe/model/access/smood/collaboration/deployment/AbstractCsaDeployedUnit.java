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
package com.braintribe.model.access.smood.collaboration.deployment;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import com.braintribe.model.access.collaboration.CollaborativeSmoodAccess;
import com.braintribe.model.access.collaboration.CsaStatePersistence;
import com.braintribe.model.cortexapi.access.collaboration.CollaborativePersistenceRequest;
import com.braintribe.model.csa.CollaborativeSmoodConfiguration;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.utils.FileTools;

/**
 * @author peter.gazdik
 */
public abstract class AbstractCsaDeployedUnit<CSA extends CollaborativeSmoodAccess> {

	public static String trunkStageName = "trunk";
	public static String resourcesFolderName = "RESOURCES";

	public CSA csa;
	public File baseFolder;
	public File jsonFile;
	public File resourcesBaseAbsoluteFile;

	public CsaStatePersistence statePersistence;
	public Supplier<CollaborativeSmoodConfiguration> configurationSupplier;
	public CollaborativeSmoodConfiguration configuration;
	public Supplier<PersistenceGmSession> sessionFactory;
	public PersistenceGmSession session;

	public void postConstruct() {
		csa.postConstruct();
		session = newSession();
	}

	public PersistenceGmSession newSession() {
		return sessionFactory.get();
	}

	public final void cleanup() {
		try {
			FileTools.deleteDirectoryRecursively(baseFolder);

		} catch (IOException e) {
			throw new GenericModelException("Deleting temporary folder failed: " + baseFolder.getAbsolutePath(), e);
		}
	}

	public File stageManFile(String stageName) {
		return stageManFile(stageName, false);
	}

	public File stageManFile(String stageName, boolean isModel) {
		File stageFolder = new File(baseFolder, stageName);

		String contentFileName = isModel ? "model.man" : "data.man";
		return new File(stageFolder, contentFileName);
	}

	public <T> T eval(CollaborativePersistenceRequest request) {
		return (T) csa.processCustomRequest(null, request);
	}

	public File resourceFile(String relativePath) {
		return new File(resourcesBaseAbsoluteFile, relativePath);
	}

}
