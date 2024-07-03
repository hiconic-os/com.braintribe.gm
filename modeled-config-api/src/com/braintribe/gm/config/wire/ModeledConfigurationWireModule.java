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
package com.braintribe.gm.config.wire;

import com.braintribe.gm.config.impl.DefaultModeledConfiguration;
import com.braintribe.gm.config.wire.contract.ModeledConfigurationContract;
import com.braintribe.gm.config.wire.space.ModeledConfigurationSpace;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.context.WireContextBuilder;
import com.braintribe.wire.api.module.WireModule;

/**
 * {@link WireModule}s that want to use external configuration given by modeled data can depend on this module and then 
 * {@link Import import} {@link ModeledConfigurationContract} to retrieved configuration instances via:
 * 
 * <ul>
 * 	<li>{@link ModeledConfigurationContract#config()}
 * 	<li>{@link ModeledConfigurationContract#config(com.braintribe.model.generic.reflection.EntityType)}
 * 	<li>{@link ModeledConfigurationContract#configReasoned(com.braintribe.model.generic.reflection.EntityType)}
 * </ul>
 * 
 * <p>
 * The default {@link ModeledConfigurationSpace binding} of {@link ModeledConfigurationContract} uses the {@link DefaultModeledConfiguration} implementation.
 * 
 * <p>
 * Other modules can override the default {@link ModeledConfigurationSpace binding} of {@link ModeledConfigurationContract} using {@link WireContextBuilder#bindContract(Class, com.braintribe.wire.api.space.WireSpace)}.
 * to supply
 * @author dirk.scheffler
 *
 */
public enum ModeledConfigurationWireModule implements WireModule {
	INSTANCE
}