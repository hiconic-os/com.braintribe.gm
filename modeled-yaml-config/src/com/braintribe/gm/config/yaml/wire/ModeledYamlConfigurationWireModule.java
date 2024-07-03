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
package com.braintribe.gm.config.yaml.wire;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.braintribe.gm.config.api.ModeledConfiguration;
import com.braintribe.gm.config.wire.ModeledConfigurationWireModule;
import com.braintribe.gm.config.wire.contract.ModeledConfigurationContract;
import com.braintribe.gm.config.yaml.ModeledYamlConfiguration;
import com.braintribe.gm.config.yaml.wire.contract.YamlConfigurationsLocationContract;
import com.braintribe.gm.config.yaml.wire.space.ModeledYamlConfigurationSpace;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.wire.api.context.WireContextBuilder;
import com.braintribe.wire.api.module.WireModule;

/**
 * This {@link WireModule} can be used to override {@link ModeledConfigurationContract} with an {@link ModeledYamlConfiguration implementation}
 * of {@link ModeledConfiguration} that uses the filesystem and yaml marshalled modeled data to retrieve configurations.
 * 
 * <p>
 * The lookup strategy is to build a filename using kebab cased variant of the {@link EntityType#getShortName() type short name} suffixed with .yaml
 * located in the config directory given by the parameter of the {@link ModeledYamlConfigurationWireModule#ModeledYamlConfigurationWireModule(File) constructor parameter}. 
 * 
 * @see ModeledYamlConfiguration
 * 
 * @author dirk.scheffler
 */
public class ModeledYamlConfigurationWireModule implements WireModule {
	private File configDir;

	/**
	 * @param configDir the directory in which configuration files are read 
	 */
	public ModeledYamlConfigurationWireModule(File configDir) {
		super();
		this.configDir = configDir;
	}
	
	@Override
	public List<WireModule> dependencies() {
		return Collections.singletonList(ModeledConfigurationWireModule.INSTANCE);
	}
	
	@Override
	public void configureContext(WireContextBuilder<?> contextBuilder) {
		WireModule.super.configureContext(contextBuilder);
		contextBuilder.bindContract(ModeledConfigurationContract.class, ModeledYamlConfigurationSpace.class);
		contextBuilder.bindContract(YamlConfigurationsLocationContract.class, () -> configDir);
	}
}