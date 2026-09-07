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
package com.braintribe.gm.config.assembly.model;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Machine-readable account of the static configuration closure produced for an application terminal. */
public interface ConfigurationAssemblyReport extends GenericEntity {

	EntityType<ConfigurationAssemblyReport> T = EntityTypes.T(ConfigurationAssemblyReport.class);

	@Initializer("[]")
	List<ConfigurationImport> getImports();
	void setImports(List<ConfigurationImport> imports);

	@Initializer("[]")
	List<String> getUnresolvedVariables();
	void setUnresolvedVariables(List<String> unresolvedVariables);

	@Initializer("[]")
	List<String> getUndeclaredVariables();
	void setUndeclaredVariables(List<String> undeclaredVariables);

	/** Symbolic inputs which the RX platform itself supplies rather than the deployment. */
	@Initializer("[]")
	List<String> getPlatformVariables();
	void setPlatformVariables(List<String> platformVariables);

	@Initializer("[]")
	List<String> getAssembledConfigurations();
	void setAssembledConfigurations(List<String> assembledConfigurations);

	@Initializer("[]")
	List<String> getResidualResources();
	void setResidualResources(List<String> residualResources);
}
