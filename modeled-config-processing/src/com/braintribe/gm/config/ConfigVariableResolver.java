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
package com.braintribe.gm.config;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

import com.braintribe.cfg.Configurable;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.config.ConfigurationEvaluationError;
import com.braintribe.gm.model.reason.config.UnresolvedProperty;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.ve.api.VirtualEnvironment;
import com.braintribe.ve.impl.StandardEnvironment;

public class ConfigVariableResolver {

	private static final String ENV_PREFIX = "env.";
	private String dirProperty = ".";
	private String fileProperty = "";
	private File file;
	private VirtualEnvironment virtualEnvironment;
	private Function<String, String> variableResolver = n -> null;

	public ConfigVariableResolver() {
		this(null);
	}
	
	public ConfigVariableResolver(File file) {
		this(StandardEnvironment.INSTANCE, file);
	}
	
	public ConfigVariableResolver(VirtualEnvironment virtualEnvironment, File file) {
		super();
		this.virtualEnvironment = virtualEnvironment;
		this.file = file;
		
		if (file != null) {
			Path filePath = file.toPath().toAbsolutePath().normalize();
			this.dirProperty = filePath.getParent().toString();
			this.fileProperty = filePath.toString();
		}
	}

	@Configurable
	public void setVariableResolver(Function<String, String> variableResolver) {
		this.variableResolver = variableResolver;
	}
	
	private class ResolutionContext implements Function<Variable, Object> {
		private Reason failure;
		
		public Object apply(Variable var) {
			return resolve(var.getName());
		}

		private String resolve(String var) {
			if (var.startsWith(ENV_PREFIX)) {
				String envName = var.substring(ENV_PREFIX.length());

				String value = virtualEnvironment.getEnv(envName);

				if (value == null) {
					acquireFailure().getReasons().add(Reasons.build(UnresolvedProperty.T) //
							.text("Could not resolve property " + var) //
							.toReason());
					return "${" + var + "}";
				}

				// return var;
				return value;
			}
			
			switch (var) {
			case "config.base":
			case "config.dir":
				return dirProperty;
			case "config.file":
				return fileProperty;
			default:
				break;
			}

			String value = variableResolver.apply(var);

			if (value != null)
				return value;

			value = virtualEnvironment.getProperty(var);

			if (value == null) {
				acquireFailure().getReasons().add(UnresolvedProperty.create(var)); //
				return "${" + var + "}";
			}

			return value;
		}
		
		public Reason getFailure() {
			return failure;
		}
		
		private Reason acquireFailure() {
			if (failure == null) {
				String msg = file != null?
						"Configuration evaluation failed for " + file.getAbsolutePath():
						"Configuration evaluation failed";
				
				failure = Reasons.build(ConfigurationEvaluationError.T)
						.text(msg).toReason();
			}

			return failure;
		}
	}


	public <E> Maybe<E> resolvePlaceholders(E config) {
		
		ResolutionContext resolutionContext = new ResolutionContext();
		
		Maybe<E> maybe = ConfigPlaceholders.resolvePlaceholders(config, resolutionContext);
		if (maybe.isUnsatisfied())
			return maybe;
		
		if (resolutionContext.getFailure() != null)
			return resolutionContext.getFailure().asMaybe();
		
		return maybe;
	}
}