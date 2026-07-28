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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.braintribe.gm.config.yaml.api.PartiallyResolvedConfiguration;
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
	private VirtualEnvironment virtualEnvironment = StandardEnvironment.INSTANCE;
	private Function<String, Maybe<String>> variableResolver = null;
	private boolean shouldAbsentify;

	public ModeledYamlConfigurationLoader virtualEnvironment(VirtualEnvironment virtualEnvironment) {
		this.virtualEnvironment = virtualEnvironment;
		return this;
	}

	public ModeledYamlConfigurationLoader variableResolver(Function<String, String> variableResolver) {
		this.variableResolver = PropertyResolutions.reasonifyPropertyResolver(variableResolver);
		return this;
	}

	public ModeledYamlConfigurationLoader variableResolverReasoned(Function<String, Maybe<String>> variableResolver) {
		this.variableResolver = variableResolver;
		return this;
	}

	public ModeledYamlConfigurationLoader absentifyMissingProperties(boolean shouldAbsentify) {
		this.shouldAbsentify = shouldAbsentify;
		return this;
	}

	public <C extends GenericEntity> Maybe<C> loadConfig(EntityType<C> configType, InputStreamProvider inputStreamProvider) {
		ConfigVariableResolver configVariableResolver = new ConfigVariableResolver(virtualEnvironment, null);
		if (variableResolver != null)
			configVariableResolver.setVariableResolverReasoned(variableResolver);

		try (InputStream in = inputStreamProvider.openInputStream()) {
			Maybe<C> maybe = YamlConfigurations.<C> read(configType) //
					.placeholders(configVariableResolver::resolve) //
					.absentifyMissingProperties(shouldAbsentify) //
					.from(in);

			if (configVariableResolver.getFailure() != null)
				return configVariableResolver.getFailure().asMaybe();

			return maybe;

		} catch (IOException e) {
			throw new UncheckedIOException(e);

		} catch (ReasonException e) {
			return e.getReason().asMaybe();
		}
	}

	/**
	 * Reads a configuration for build-time assembly. Available variables are resolved, while genuinely unavailable variables remain represented by
	 * value descriptors and are reported in the result. Runtime loading must continue to use {@link #loadConfig(EntityType, InputStreamProvider)},
	 * which fails for every unresolved variable.
	 */
	public <C extends GenericEntity> Maybe<PartiallyResolvedConfiguration<C>> loadConfigPartially(EntityType<C> configType,
			InputStreamProvider inputStreamProvider) {
		ConfigVariableResolver configVariableResolver = newConfigVariableResolver(null);

		try (InputStream in = inputStreamProvider.openInputStream()) {
			return loadConfigPartially(configType, in, configVariableResolver);

		} catch (IOException e) {
			throw new UncheckedIOException(e);

		} catch (ReasonException e) {
			return e.getReason().asMaybe();
		}
	}

	public <C extends GenericEntity> Maybe<PartiallyResolvedConfiguration<C>> loadConfigPartially(EntityType<C> configType, File configFile,
			boolean fileMustExist) {
		if (!configFile.exists()) {
			if (fileMustExist)
				return Reasons.build(NotFound.T).text("Configuration file " + configFile.getAbsolutePath() + " does not exist").toMaybe();

			return Maybe.complete(new PartiallyResolvedConfiguration<>(configType.create(), Set.of()));
		}

		ConfigVariableResolver configVariableResolver = newConfigVariableResolver(configFile);

		try (InputStream in = new FileInputStream(configFile)) {
			return loadConfigPartially(configType, in, configVariableResolver);

		} catch (IOException e) {
			throw new UncheckedIOException(e);

		} catch (ReasonException e) {
			return e.getReason().asMaybe();
		}
	}

	private ConfigVariableResolver newConfigVariableResolver(File configFile) {
		ConfigVariableResolver configVariableResolver = new ConfigVariableResolver(virtualEnvironment, configFile);
		if (variableResolver != null)
			configVariableResolver.setVariableResolverReasoned(variableResolver);
		return configVariableResolver;
	}

	private <C extends GenericEntity> Maybe<PartiallyResolvedConfiguration<C>> loadConfigPartially(EntityType<C> configType, InputStream in,
			ConfigVariableResolver configVariableResolver) {
		Maybe<C> configMaybe = YamlConfigurations.<C> read(configType) //
				.placeholders() //
				.absentifyMissingProperties(shouldAbsentify) //
				.from(in);

		if (configMaybe.isUnsatisfied())
			return configMaybe.whyUnsatisfied().asMaybe();

		return new PartialConfigPlaceholderResolver(configVariableResolver).resolve(configMaybe.get());
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
		variableResolver.setVariableResolverReasoned(this.variableResolver);
		Maybe<C> maybe = YamlConfigurations.<C> read(configType) //
				.placeholders(variableResolver::resolve) //
				.from(configFile);

		if (variableResolver.getFailure() != null) {
			return variableResolver.getFailure().asMaybe();
		}

		return maybe;
	}
}
