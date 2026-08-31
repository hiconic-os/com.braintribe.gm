// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
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

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.braintribe.gm.config.yaml.api.PartiallyResolvedConfiguration;
import com.braintribe.gm.config.yaml.model.LoadedEntity;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.config.ConfigurationError;
import com.braintribe.gm.model.reason.config.ConfigurationEvaluationError;
import com.braintribe.gm.model.reason.config.PropertyNotFound;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.model.bvd.convert.ToInteger;
import com.braintribe.model.bvd.convert.ToString;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;

public class ModeledYamlConfigurationLoaderTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void strictRuntimeReadFailsForMissingVariables() {
		Maybe<LoadedEntity> result = loader().loadConfig(LoadedEntity.T, yaml("cpValue: ${MISSING}"));

		assertThat(result).isUnsatisfiedBy(ConfigurationEvaluationError.T);
		assertThat(result.whyUnsatisfied().getReasons()).hasSize(1);
		assertThat(result.whyUnsatisfied().getReasons().get(0)).isInstanceOf(PropertyNotFound.class);
	}

	@Test
	public void partialBuildReadResolvesKnownAndRetainsUnknownVariables() {
		ModeledYamlConfigurationLoader loader = loader().variableResolver(name -> switch (name) {
		case "KNOWN" -> "resolved";
		default -> null;
		});

		Maybe<PartiallyResolvedConfiguration<LoadedEntity>> result = loader.loadConfigPartially(LoadedEntity.T,
				yaml("""
						cpValue: ${KNOWN}-${MISSING}
						integerValue: ${PORT}
						"""));

		assertThat(result).isSatisfied();
		PartiallyResolvedConfiguration<LoadedEntity> partial = result.get();
		assertThat(partial.unresolvedVariables()).containsExactlyInAnyOrder("MISSING", "PORT");

		ValueDescriptor cpValue = LoadedEntity.T.getProperty("cpValue").getVdDirect(partial.configuration());
		assertThat(cpValue).isInstanceOf(ToString.class);
		assertThat(((ToString) cpValue).getOperand()).isInstanceOf(Concatenation.class);
		Concatenation concatenation = (Concatenation) ((ToString) cpValue).getOperand();
		assertThat(concatenation.getOperands()).hasSize(3);
		assertThat(concatenation.getOperands().get(0)).isEqualTo("resolved");
		assertThat(concatenation.getOperands().get(1)).isEqualTo("-");
		assertVariable(concatenation.getOperands().get(2), "MISSING");

		ValueDescriptor integerValue = LoadedEntity.T.getProperty("integerValue").getVdDirect(partial.configuration());
		assertThat(integerValue).isInstanceOf(ToInteger.class);
		assertVariable(((ToInteger) integerValue).getOperand(), "PORT");
	}

	@Test
	public void reasonedPartialReadMatchesEstablishedPartialRead() {
		String source = """
				cpValue: ${KNOWN}-${MISSING}
				integerValue: ${PORT}
				""";
		ModeledYamlConfigurationLoader loader = loader().variableResolver(name -> switch (name) {
		case "KNOWN" -> "resolved";
		default -> null;
		});

		PartiallyResolvedConfiguration<LoadedEntity> established = loader.loadConfigPartially(LoadedEntity.T, yaml(source)).get();
		LoadedEntity unresolved = YamlConfigurations.<LoadedEntity> read(LoadedEntity.T).placeholders().from(yaml(source)).get();
		PartiallyResolvedConfiguration<LoadedEntity> reasoned = YamlConfigurations.resolvePlaceholdersPartiallyReasoned(unresolved,
				variable -> "KNOWN".equals(variable.getName())
						? Maybe.complete("resolved")
						: PropertyNotFound.create(variable.getName()).asMaybe()).get();

		assertThat(reasoned.unresolvedVariables()).containsExactlyInAnyOrderElementsOf(established.unresolvedVariables());
		assertPartialValues(reasoned.configuration());
		assertPartialValues(established.configuration());
	}

	@Test
	public void partialBuildReadEvaluatesClosedTypedExpressions() {
		ModeledYamlConfigurationLoader loader = loader().variableResolver(name -> name.equals("PORT") ? "4711" : null);

		Maybe<PartiallyResolvedConfiguration<LoadedEntity>> result = loader.loadConfigPartially(LoadedEntity.T,
				yaml("integerValue: ${PORT}"));

		assertThat(result).isSatisfied();
		assertThat(result.get().unresolvedVariables()).isEmpty();
		assertThat(result.get().configuration().getIntegerValue()).isEqualTo(4711);
	}

	@Test
	public void partialBuildReadDoesNotHideResolverFailures() {
		ModeledYamlConfigurationLoader loader = loader().variableResolverReasoned(name -> Reasons.build(ConfigurationError.T)
				.text("Resolver failed").toMaybe());

		Maybe<PartiallyResolvedConfiguration<LoadedEntity>> result = loader.loadConfigPartially(LoadedEntity.T,
				yaml("cpValue: ${BROKEN}"));

		assertThat(result).isUnsatisfiedBy(ConfigurationError.T);
	}

	@Test
	public void partialBuildReadDoesNotHideTypeConversionFailures() {
		ModeledYamlConfigurationLoader loader = loader().variableResolver(name -> "not-an-integer");

		Maybe<PartiallyResolvedConfiguration<LoadedEntity>> result = loader.loadConfigPartially(LoadedEntity.T,
				yaml("integerValue: ${PORT}"));

		assertThat(result).isUnsatisfiedBy(InternalError.T);
	}

	@Test
	public void partialFileReadResolvesSourceLocationVariables() throws Exception {
		File configFile = temporaryFolder.newFile("loaded-entity.yaml");
		Files.writeString(configFile.toPath(), "cpValue: ${config.dir}", StandardCharsets.UTF_8);

		Maybe<PartiallyResolvedConfiguration<LoadedEntity>> result = loader().loadConfigPartially(LoadedEntity.T, configFile, true);

		assertThat(result).isSatisfied();
		assertThat(result.get().unresolvedVariables()).isEmpty();
		assertThat(result.get().configuration().getCpValue()).isEqualTo(configFile.getParentFile().getAbsolutePath());
	}

	private static ModeledYamlConfigurationLoader loader() {
		return new ModeledYamlConfigurationLoader();
	}

	private static InputStreamProvider yaml(String yaml) {
		byte[] bytes = yaml.getBytes(StandardCharsets.UTF_8);
		return () -> new ByteArrayInputStream(bytes);
	}

	private static void assertVariable(Object value, String name) {
		assertThat(value).isInstanceOf(Variable.class);
		assertThat(((Variable) value).getName()).isEqualTo(name);
	}

	private static void assertPartialValues(LoadedEntity configuration) {
		ValueDescriptor cpValue = LoadedEntity.T.getProperty("cpValue").getVdDirect(configuration);
		assertThat(cpValue).isInstanceOf(ToString.class);
		Concatenation concatenation = (Concatenation) ((ToString) cpValue).getOperand();
		assertThat(concatenation.getOperands()).hasSize(3);
		assertThat(concatenation.getOperands().get(0)).isEqualTo("resolved");
		assertThat(concatenation.getOperands().get(1)).isEqualTo("-");
		assertVariable(concatenation.getOperands().get(2), "MISSING");

		ValueDescriptor integerValue = LoadedEntity.T.getProperty("integerValue").getVdDirect(configuration);
		assertThat(integerValue).isInstanceOf(ToInteger.class);
		assertVariable(((ToInteger) integerValue).getOperand(), "PORT");
	}
}
