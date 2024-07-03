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

import com.braintribe.model.csa.CollaborativeSmoodConfiguration;

/**
 * Component that is responsible for storing and loading CSA configuration and (in one case) it's state (see {@link #readMarker()}).
 * 
 * @author peter.gazdik
 */
public interface CsaStatePersistence {

	/**
	 * Loads the CSA configuration from config.json AND ALSO stores that configuration as the original configuration, in case no original
	 * configuration exists yet.
	 */
	CollaborativeSmoodConfiguration readConfiguration();
	void writeConfiguration(CollaborativeSmoodConfiguration value);

	/**
	 * Reads the original CSA configuration - this is used when the whole persistence is being reset, and the original configuration was (most likely)
	 * created as a copy of regular configuration when it was first accessed.
	 */
	CollaborativeSmoodConfiguration readOriginalConfiguration();
	void overwriteOriginalConfiguration(CollaborativeSmoodConfiguration value);

	/** Loads a marker used by the DCSA; marks which operations from the shared storage were already processed. */
	String readMarker();
	void writeMarker(String marker);

}
