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
package com.braintribe.model.access.collaboration;

import com.braintribe.cfg.Required;
import com.braintribe.model.csa.CollaborativeSmoodConfiguration;
import com.braintribe.model.csa.ManInitializer;
import com.braintribe.utils.file.api.PathValueStore;

/**
 * @author peter.gazdik
 */
public class CsaStatePersistenceImpl implements CsaStatePersistence {

	private PathValueStore keyValueStore;

	private static final String configJson = "config.json";
	private static final String configOriginalJson = "config.original.json";
	private static final String markerTxt = "marker.txt";

	@Required
	public void setPathValueStore(PathValueStore keyValueStore) {
		this.keyValueStore = keyValueStore;
	}

	@Override
	public CollaborativeSmoodConfiguration readConfiguration() {
		CollaborativeSmoodConfiguration result = read(configJson);
		if (result == null) {
			result = defaultCsaConfiguration();
			write(configJson, result);
		}

		if (!keyValueStore.hasEntry(configOriginalJson))
			write(configOriginalJson, result);

		return result;
	}

	@Override
	public CollaborativeSmoodConfiguration readOriginalConfiguration() {
		CollaborativeSmoodConfiguration result = read(configOriginalJson);
		return result == null ? readConfiguration() : result;
	}

	@Override
	public void writeConfiguration(CollaborativeSmoodConfiguration value) {
		write(configJson, value);
	}

	@Override
	public void overwriteOriginalConfiguration(CollaborativeSmoodConfiguration value) {
		write(configJson, value);
		write(configOriginalJson, value);
	}

	public static CollaborativeSmoodConfiguration defaultCsaConfiguration() {
		ManInitializer trunk = ManInitializer.T.create();
		trunk.setName("trunk");
		CollaborativeSmoodConfiguration result = CollaborativeSmoodConfiguration.T.create();
		result.getInitializers().add(trunk);

		return result;
	}

	@Override
	public String readMarker() {
		return read(markerTxt);
	}

	@Override
	public void writeMarker(String marker) {
		write(markerTxt, marker);
	}

	private <T> T read(String fileName) {
		return keyValueStore.read(fileName);
	}

	private void write(String fileName, Object value) {
		keyValueStore.write(fileName, value);
	}

}
