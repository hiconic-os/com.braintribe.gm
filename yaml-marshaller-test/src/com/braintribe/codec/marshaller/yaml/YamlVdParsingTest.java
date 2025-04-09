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
package com.braintribe.codec.marshaller.yaml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.braintribe.codec.marshaller.yaml.model.TestEntity;
import com.braintribe.gm.config.yaml.YamlConfigurations;
import com.braintribe.gm.model.reason.Maybe;

public class YamlVdParsingTest {
	
	@Test
	public void testPlaceholderConfiguration() {
		Map<String, Object> vars = new HashMap<>();
		
		vars.put("longValue", "5");
		vars.put("intValue", "23");
		vars.put("listIntValue1", "1");
		vars.put("listIntValue2", "3");
		
		Maybe<TestEntity> entityMaybe = YamlConfigurations.read(TestEntity.T).placeholders(v -> vars.get(v.getName())).from(new File("res/vd-test.yaml"));
		
		TestEntity resultEntity = entityMaybe.get();
		
		assertThat(resultEntity.getLongValue()).isEqualTo(5L);
		assertThat(resultEntity.getIntValue()).isEqualTo(23);
		assertThat(resultEntity.getStringValue()).isEqualTo("$escape-test");
		assertThat(resultEntity.getIntegerList()).isEqualTo(Arrays.asList(1, 2, 3));
	}
	
	@Test
	public void testPlaceholderAndAbsence() {
		Map<String, Object> vars = new HashMap<>();
		
		vars.put("longValue", "5");
		vars.put("intValue", "23");
		vars.put("listIntValue1", "1");
		vars.put("listIntValue2", "3");
		
		Maybe<TestEntity> entityMaybe = YamlConfigurations.read(TestEntity.T) //
				.absentifyMissingProperties() //
				.placeholders(v -> vars.get(v.getName())) //
				.from(new File("res/vd-test.yaml")); 
		
		TestEntity resultEntity = entityMaybe.get();
		
		assertThat(resultEntity.getLongValue()).isEqualTo(5L);
		assertThat(resultEntity.getIntValue()).isEqualTo(23);
		assertThat(resultEntity.getStringValue()).isEqualTo("$escape-test");
		assertThat(resultEntity.getIntegerList()).isEqualTo(Arrays.asList(1, 2, 3));
	}
}
