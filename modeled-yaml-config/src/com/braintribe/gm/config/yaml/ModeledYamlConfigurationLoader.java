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
package com.braintribe.gm.config.yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Function;
import java.util.function.Supplier;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.ReasonException;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.ve.api.VirtualEnvironment;
import com.braintribe.ve.impl.StandardEnvironment;

public class ModeledYamlConfigurationLoader {
	private VirtualEnvironment virtualEnvironment = StandardEnvironment.INSTANCE ;
	private Function<String, String> variableResolver = n -> null;
	
	
	public ModeledYamlConfigurationLoader virtualEnvironment(VirtualEnvironment virtualEnvironment) {
		this.virtualEnvironment = virtualEnvironment;
		return this;
	}
	
	public ModeledYamlConfigurationLoader variableResolver(Function<String, String> variableResolver) {
		this.variableResolver = variableResolver;
		return this;
	}
	
	public <C extends GenericEntity> Maybe<C> loadConfig(EntityType<C> configType, InputStreamProvider inputStreamProvider) {
		ConfigVariableResolver variableResolver = new ConfigVariableResolver(virtualEnvironment, null);
		variableResolver.setVariableResolver(this.variableResolver);
		
		try (InputStream in = inputStreamProvider.openInputStream()) {
			Maybe<C> maybe = YamlConfigurations.<C>read(configType) //
					.placeholders(variableResolver::resolve) //
					.from(inputStreamProvider);
			
			if (variableResolver.getFailure() != null) {
				return variableResolver.getFailure().asMaybe();
			}
			
			return maybe;
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		catch (ReasonException e) {
			return e.getReason().asMaybe();
		}
	}
	
	public <C extends GenericEntity> Maybe<C> loadConfig(EntityType<C> configType, File configFile, boolean fileMustExist) {
		return loadConfig(configType, configFile, configType::create, fileMustExist);
	}
	
	public <C> Maybe<C> loadConfig(GenericModelType configType, File configFile, Supplier<C> defaultSupplier, boolean fileMustExist) {
		// if file does not exist a default instance of the configuration will be created
		if (!configFile.exists()) {
			if (fileMustExist) 
				return Reasons.build(NotFound.T).text("Configuration file " + configFile.getAbsolutePath() + " does not exist").toMaybe();
			else 
				return Maybe.complete(defaultSupplier.get());
		}
		
		ConfigVariableResolver variableResolver = new ConfigVariableResolver(virtualEnvironment, configFile);
		variableResolver.setVariableResolver(this.variableResolver);
		Maybe<C> maybe = YamlConfigurations.<C>read(configType) //
				.placeholders(variableResolver::resolve) //
				.from(configFile);
		
		if (variableResolver.getFailure() != null) {
			return variableResolver.getFailure().asMaybe();
		}
		
		return maybe;
	}
}
