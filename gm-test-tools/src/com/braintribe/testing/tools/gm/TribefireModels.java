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
package com.braintribe.testing.tools.gm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.braintribe.model.access.impl.XmlBasedModelDataProvider;
import com.braintribe.model.meta.GmMetaModel;

/**
 * Provides methods to get tribefire (core) models.
 */
public class TribefireModels {

	public static GmMetaModel getBasicDeploymentModel() throws Exception {
		return getCoreModel("tribefire.cortex:basic-deployment-model#1.2");
	}

	public static GmMetaModel getCoreModel(String name) throws Exception {
		ClassLoader classLoader = TribefireModels.class.getClassLoader();
		BufferedReader indexReader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("models/index.txt"), "ISO-8859-1"));

		try {

			StringBuffer indexContent = new StringBuffer();
			String line = null;
			while ((line = indexReader.readLine()) != null) {

				indexContent.append(line);
				String[] elements = line.split(",");

				String modelXml = elements[1];

				URL modelUrl = classLoader.getResource("models/" + modelXml);

				if (modelUrl != null) {
					XmlBasedModelDataProvider<GmMetaModel> modelProvider = new XmlBasedModelDataProvider<>();
					modelProvider.setUrl(modelUrl);

					String modelName = elements[0];
					if (modelName.equals(name)) {
						return modelProvider.get();
					}
				}
			}

			throw new RuntimeException("Core model " + name + " not found.");
		} finally {
			indexReader.close();
		}
	}
}
