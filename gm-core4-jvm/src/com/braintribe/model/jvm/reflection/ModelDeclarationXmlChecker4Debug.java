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
package com.braintribe.model.jvm.reflection;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.braintribe.logging.Logger;
import com.braintribe.utils.FileTools;

/**
 * For models that are on classpath as projects there is an issue sometimes that "model-declaration.xml" is missing. This is checking that all such
 * projects do have the xml files, otherwise an exception is thrown.
 * 
 * @author peter.gazdik
 */
class ModelDeclarationXmlChecker4Debug {

	private static final Logger log = Logger.getLogger(ModelDeclarationXmlChecker4Debug.class);

	public static void run() {
		new ModelDeclarationXmlChecker4Debug().checkModelProjectsHaveModDecXml();
	}

	private void checkModelProjectsHaveModDecXml() {
		ClassLoader cl = ModelDeclarationXmlChecker4Debug.class.getClassLoader();
		if (!(cl instanceof URLClassLoader)) {
			log.warn("Cannot check models on classpath, ClassLoader is not an instance of URLClassLoadeer, but: " + cl.getClass().getName());
			return;
		}

		URL[] urls = ((URLClassLoader) cl).getURLs();

		Map<File, List<File>> projectToOutputs = Stream.of(urls) //
				.map(url -> new File(url.getPath())) //
				.filter(File::isDirectory) //
				.collect(Collectors.groupingBy(File::getParentFile));

		for (Entry<File, List<File>> e : projectToOutputs.entrySet())
			if (isModelArtifactDir(e.getKey()))
				checkContainsModDecXml(e.getKey(), e.getValue());
	}

	private boolean isModelArtifactDir(File folder) {
		File pomXml = new File(folder, "pom.xml");
		return pomXml.exists() && FileTools.read(pomXml).asString().contains("<archetype>model</archetype>");
	}

	private void checkContainsModDecXml(File projectDir, List<File> projectOutputDirs) {
		for (File dir : projectOutputDirs)
			if (new File(dir, "model-declaration.xml").exists())
				return;

		throw new IllegalStateException("CANNOT START TRIBEFIRE SERVER!!!\n\t"//
				+ "[model-declaration.xml] not found for model project: " + projectDir.getName() + ".\n\t" + //
				"This xml should be auto-generated in Project's build output folder by the DevRock Model Builder plugin. " //
				+ "Please try to clean this project.\n\t" + //
				"Checked output dirs:\n\t\t" + projectOutputDirs.stream().map(File::getName).collect(Collectors.joining("\n\t\t")));
	}

}
