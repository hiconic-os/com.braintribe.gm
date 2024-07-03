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
package com.braintribe.model.generic.reflection.cloning;

import org.junit.Test;

import com.braintribe.model.generic.reflection.ConfigurableCloningContext;
import com.braintribe.model.generic.reflection.cloning.model.City;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.processing.session.impl.notifying.BasicNotifyingGmSession;

/**
 * @author peter.gazdik
 */
public class SimpleCloning_Configurable_Test extends SimpleCloning_Base {

	@Test
	@Override
	public void simplyCopying() {
		cc = new ConfigurableCloningContext();

		runSimplyCopying();
	}

	@Test
	@Override
	public void copyOnASession() {
		GmSession session = new BasicNotifyingGmSession();

		cc = ConfigurableCloningContext.build().supplyRawCloneWith(session).done();

		runCopyOnASession(session);
	}

	@Test
	@Override
	public void doNotCopyIdStuff() {
		cc = ConfigurableCloningContext.build().skipIndentifying(true).skipGlobalId(true).done();

		runDoNotCopyIdStuff();
	}

	@Test
	@Override
	public void referenceOriginalPropertyValue() {
		cc = ConfigurableCloningContext.build().withAssociatedResolver(e -> e instanceof City ? e : null).done();

		runReferenceOriginalPropertyValue();
	}

	@Test
	@Override
	public void stringifyIdInPreProcess() {
		cc = ConfigurableCloningContext.build().withOriginPreProcessor(super::stringifyId).done();

		runStringifyIdInPreProcess();
	}

	@Test
	@Override
	public void stringifyIdInPostProcess() {
		cc = ConfigurableCloningContext.build().withClonedValuePostProcesor((type, o) -> o instanceof Long ? "" + o : o).done();

		runStringifyIdInPostProcess();
	}

}
