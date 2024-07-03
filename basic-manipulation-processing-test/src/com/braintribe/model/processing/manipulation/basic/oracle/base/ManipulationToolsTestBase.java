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
package com.braintribe.model.processing.manipulation.basic.oracle.base;

import com.braintribe.model.access.smood.basic.SmoodAccess;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.VoidManipulation;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.test.tools.meta.ManipulationDriver;
import com.braintribe.testing.tools.gm.GmTestTools;

/**
 * @author peter.gazdik
 */
public class ManipulationToolsTestBase {

	protected static VoidManipulation VOID_MANIPULATION = VoidManipulation.T.create();
	protected static String ACCESS_ID = "access.test.manipulations";

	protected Manipulation manipulation;
	protected Manipulation inducedManipulation = VOID_MANIPULATION;

	protected SmoodAccess access = GmTestTools.newSmoodAccessMemoryOnly(ACCESS_ID, null);
	protected PersistenceGmSession session = newSession();

	protected final ManipulationDriver md = new ManipulationDriver();

	protected PersistenceGmSession newSession() {
		return GmTestTools.newSession(access);
	}

	public ManipulationToolsTestBase() {
		/* This is important, otherwise the explicit partition assignments are ignored and induced manipulations are
		 * generated, which leads to problems when applying these induced manipulations */
		access.getDatabase().setIgnorePartitions(false);
	}

}
