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

import org.junit.Test;

import com.braintribe.gm.config.yaml.index.ClasspathIndex;
import com.braintribe.gm.config.yaml.model.LoadedEntity;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

/**
 * Tests for {@link ModeledYamlConfiguration}.
 */
public class ModeledYamlConfigurationTest {

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

		assertThat(entity).hasNotAbsentProperty("preCpValue"); // test that absence information used internally is cleared
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

}
