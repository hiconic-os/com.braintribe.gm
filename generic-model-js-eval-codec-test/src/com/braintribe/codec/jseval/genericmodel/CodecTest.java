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
package com.braintribe.codec.jseval.genericmodel;

import java.util.Date;
import java.util.Set;

import org.junit.Test;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.testing.test.AbstractTest;

public class CodecTest extends AbstractTest {
	@Test
	public void test() {
		AbsenceInformation ai = GMF.absenceInformation();
		ai.setSize(50);

		SomeEntity someEntity = SomeEntity.T.create();
		someEntity.entityType().getProperty("hint").setAbsenceInformation(someEntity, ai);
		someEntity.setName("noname");
		someEntity.setCreated(new Date());
		someEntity.setHint("don't look back");
		long s, e;

		s = System.currentTimeMillis();

		// JavaScriptPrototypes prototypes = new CondensedJavaScriptPrototypes();
		JavaScriptPrototypes prototypes = new PrettyJavaScriptPrototypes();
		GenericModelJsEvalCodec<SomeEntity> codec = new GenericModelJsEvalCodec<SomeEntity>();
		codec.setPrototypes(prototypes);
		// codec.setHostedMode(true);
		String js = codec.encode(someEntity);
		e = System.currentTimeMillis();
		logger.info(js);
		logger.info("full time: " + (e - s));

		Set<String> typeNames = JseUtil.extractTypes(js);
		logger.info("Type names: " + typeNames);
	}

	public static interface SomeEntity extends GenericEntity {
		final EntityType<SomeEntity> T = EntityTypes.T(SomeEntity.class);

		// @formatter:off
		Date getCreated();
		void setCreated(Date created);

		String getName();
		void setName(String name);

		String getHint();
		void setHint(String hint);
		// @formatter:on
	}
}
