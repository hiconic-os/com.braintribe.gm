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
package com.braintribe.model.processing.manipulation.configurator;

import com.braintribe.config.configurator.Configurator;
import com.braintribe.config.configurator.ConfiguratorException;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.processing.manipulation.marshaller.ManipulationStringifier;

/**
 * @author peter.gazdik
 */
public class ManipulationStringifierConfigurator implements Configurator {

	@Override
	public void configure() throws ConfiguratorException {
		GMF.platform().registerStringifier(Manipulation.T, ManipulationStringifierConfigurator::stringifyManipulation);
	}

	private static String stringifyManipulation(Manipulation m) {
		return ManipulationStringifier.stringify(m, m.isRemote());
	}

	@Override
	public String toString() {
		return "QueryStringifierConfigurator (registering stringifier for Query and QueryPlan)";
	}

}
