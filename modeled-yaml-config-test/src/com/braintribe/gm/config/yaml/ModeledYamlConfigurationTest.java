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
package com.braintribe.gm.config.yaml;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.braintribe.gm.config.yaml.api.PartiallyResolvedConfiguration;
import com.braintribe.gm.config.yaml.index.ClasspathIndex;
import com.braintribe.gm.config.yaml.model.LoadedEntity;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.bvd.convert.ToString;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * Tests for {@link ModeledYamlConfiguration}.
 */
public class ModeledYamlConfigurationTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private final ModeledYamlConfiguration myc = new ModeledYamlConfiguration();

	@Test
	public void noConfigSource_EmptyInstance() throws Exception {
		LoadedEntity entity = load();

		assertThat(entity).isNotNull();

		assertThat(entity.getOrigin()).isNull();
		assertThat(entity.getPreCpValue()).isNull();
		assertThat(entity.getCpValue()).isNull();
		assertThat(entity.getPreFsValue()).isNull();
		assertThat(entity.getFs1Value()).isNull();
		assertThat(entity.getFs2Value()).isNull();
		assertThat(entity.getAfterAllValue()).isNull();
	}

	@Test
	public void cpOnly() throws Exception {
		setClasspathIndex();

		LoadedEntity entity = load();

		assertThat(entity).isNotNull();

		// test non-set values are not absent

		assertThat(entity).hasNotAbsentProperty("primitiveBoolean");
		assertThat(entity.getPrimitiveBoolean()).isFalse();

		assertThat(entity).hasNotAbsentProperty("preCpValue");
		assertThat(entity.getPreCpValue()).isNull();

		assertThat(entity.getCpValue()).isEqualTo("cp-value");

		assertThat(entity).hasNotAbsentProperty("preFsValue");
		assertThat(entity.getPreFsValue()).isNull();

		assertThat(entity.getFs1Value()).isNull();
		assertThat(entity.getFs2Value()).isNull();

		assertThat(entity.getAfterAllValue()).isNull();
	}

	@Test
	public void confDirOnly() throws Exception {
		setConfigFolder();

		LoadedEntity entity = load();

		assertThat(entity).isNotNull();

		assertThat(entity.getPreCpValue()).isNull();
		assertThat(entity.getCpValue()).isNull();

		assertThat(entity.getPreFsValue()).isNull();

		assertThat(entity.getFs1Value()).isEqualTo("low-prio-value");
		assertThat(entity.getFs2Value()).isEqualTo("high-prio-value");

		assertThat(entity.getAfterAllValue()).isNull();
	}

	@Test
	public void programmaticOnly() throws Exception {
		registerProgrammaticSources();

		LoadedEntity entity = load();

		assertThat(entity).isNotNull();
		assertThat(entity.getPreCpValue()).isEqualTo("pre-cp-value");
		assertThat(entity.getCpValue()).isNull();
		assertThat(entity.getPreFsValue()).isEqualTo("pre-fs-value");
		assertThat(entity.getFs1Value()).isNull();
		assertThat(entity.getFs2Value()).isNull();
		assertThat(entity.getAfterAllValue()).isEqualTo("after-all-value");
	}

	@Test
	public void mixOfAllsources() throws Exception {
		setClasspathIndex();
		setConfigFolder();
		registerProgrammaticSources();

		LoadedEntity entity = load();

		assertThat(entity).isNotNull();
		assertThat(entity.getPreCpValue()).isEqualTo("pre-cp-value");
		assertThat(entity.getCpValue()).isEqualTo("cp-value");
		assertThat(entity.getPreFsValue()).isEqualTo("pre-fs-value");
		assertThat(entity.getFs1Value()).isEqualTo("low-prio-value");
		assertThat(entity.getFs2Value()).isEqualTo("high-prio-value");
		assertThat(entity.getAfterAllValue()).isEqualTo("after-all-value");

	}

	@Test
	public void classpathValueOverridesProgrammaticVdDefault() {
		setClasspathIndex();

		LoadedEntity beforeClasspath = createAbsentEntity();
		setVariable(beforeClasspath, "cpValue", "CP_DEFAULT");
		myc.registerConfiguration("symbolic default", LoadedEntity.T, "", ConfigurationStage.beforeClasspath, 0, () -> beforeClasspath);

		LoadedEntity entity = load();

		assertThat(entity.getCpValue()).isEqualTo("cp-value");
	}

	@Test
	public void programmaticVdOverrideSurvivesClasspathMerge() {
		setClasspathIndex();

		LoadedEntity afterClasspath = createAbsentEntity();
		setVariable(afterClasspath, "cpValue", "CP_OVERRIDE");
		myc.registerConfiguration("symbolic override", LoadedEntity.T, "", ConfigurationStage.afterEverythingElse, 0, () -> afterClasspath);

		LoadedEntity entity = load();

		assertVariable(entity, "cpValue", "CP_OVERRIDE");
	}

	@Test
	public void staticPartialReadUsesRuntimeMergeAndRetainsUnknownVariables() throws Exception {
		File configFolder = temporaryFolder.newFolder("partial-conf");
		Files.writeString(new File(configFolder, "loaded-entity.low.yaml").toPath(), """
				cpValue: ${KNOWN}-${MISSING}
				fs1Value: low-priority
				""", StandardCharsets.UTF_8);
		Files.writeString(new File(configFolder, "loaded-entity.high-2.yaml").toPath(), """
				fs1Value: high-priority
				""", StandardCharsets.UTF_8);

		myc.setConfigFolder(configFolder);
		myc.setExternalPropertyLookup(name -> name.equals("KNOWN") ? "resolved" : null);

		Maybe<PartiallyResolvedConfiguration<LoadedEntity>> result = myc.staticConfigPartiallyReasoned(LoadedEntity.T);

		assertThat(result).isSatisfied();
		PartiallyResolvedConfiguration<LoadedEntity> partial = result.get();
		assertThat(partial.unresolvedVariables()).containsExactly("MISSING");
		assertThat(partial.configuration().getFs1Value()).isEqualTo("high-priority");

		ValueDescriptor descriptor = LoadedEntity.T.getProperty("cpValue").getVdDirect(partial.configuration());
		assertThat(descriptor).isInstanceOf(ToString.class);
		assertThat(((ToString) descriptor).getOperand()).isInstanceOf(Concatenation.class);
		Concatenation concatenation = (Concatenation) ((ToString) descriptor).getOperand();
		assertThat(concatenation.getOperands()).hasSize(3);
		assertThat(concatenation.getOperands().get(0)).isEqualTo("resolved");
		assertThat(concatenation.getOperands().get(1)).isEqualTo("-");
		assertThat(concatenation.getOperands().get(2)).isInstanceOf(Variable.class);
		assertThat(((Variable) concatenation.getOperands().get(2)).getName()).isEqualTo("MISSING");
	}

	@Test
	public void staticPartialReadDoesNotInitializeProgrammaticConfiguration() {
		LoadedEntity registered = createAbsentEntity();
		registered.setAfterAllValue("must-not-be-assembled");
		myc.registerConfiguration("runtime contribution", LoadedEntity.T, "", ConfigurationStage.afterEverythingElse, 0, () -> registered);

		PartiallyResolvedConfiguration<LoadedEntity> partial = myc.staticConfigPartiallyReasoned(LoadedEntity.T).get();

		assertThat(partial.configuration().getAfterAllValue()).isNull();
	}

	private void registerProgrammaticSources() {
		LoadedEntity beforeCp = createAbsentEntity();
		beforeCp.setOrigin("code-before-cp");
		beforeCp.setPreCpValue("pre-cp-value");
		beforeCp.setPreFsValue("pre-cp-value");
		beforeCp.setAfterAllValue("pre-cp-value");

		LoadedEntity beforeFs = createAbsentEntity();
		beforeFs.setOrigin("code-before-fs");
		beforeFs.setPreFsValue("pre-fs-value");
		beforeFs.setAfterAllValue("pre-fs-value");

		LoadedEntity afterAll = createAbsentEntity();
		afterAll.setOrigin("code-after-all");
		afterAll.setAfterAllValue("after-all-value");

		myc.registerConfiguration("test", LoadedEntity.T, "", ConfigurationStage.beforeClasspath, 0, () -> beforeCp);
		myc.registerConfiguration("test", LoadedEntity.T, "", ConfigurationStage.beforeConfDir, 0, () -> beforeFs);
		myc.registerConfiguration("test", LoadedEntity.T, "", ConfigurationStage.afterEverythingElse, 0, () -> afterAll);
	}

	private static LoadedEntity createAbsentEntity() {
		return ModeledYamlConfigurationTest.createAbsentEntity(LoadedEntity.T);
	}

	private LoadedEntity load() {
		return myc.config(LoadedEntity.T);
	}

	private void setClasspathIndex() {
		myc.setClasspathIndex(new ClasspathIndex());
	}

	private void setConfigFolder() {
		myc.setConfigFolder(new File("res/conf"));
	}

	public static <E extends GenericEntity> E createAbsentEntity(EntityType<E> et) {
		E e = et.createRaw();

		for (Property p : et.getProperties())
			p.setAbsenceInformation(e, GMF.absenceInformation());

		return e;
	}

	private static void setVariable(LoadedEntity entity, String propertyName, String variableName) {
		Variable variable = Variable.T.create();
		variable.setName(variableName);
		LoadedEntity.T.getProperty(propertyName).setVdDirect(entity, variable);
	}

	private static void assertVariable(LoadedEntity entity, String propertyName, String variableName) {
		ValueDescriptor descriptor = LoadedEntity.T.getProperty(propertyName).getVdDirect(entity);

		assertThat(descriptor).isInstanceOf(Variable.class);
		assertThat(((Variable) descriptor).getName()).isEqualTo(variableName);
	}

}
