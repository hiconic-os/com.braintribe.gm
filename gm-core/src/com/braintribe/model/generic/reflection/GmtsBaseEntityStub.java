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
package com.braintribe.model.generic.reflection;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.GmSystemInterface;

@GmSystemInterface
public abstract class GmtsBaseEntityStub implements GenericEntity {

	// Temporary, in case we need it. But let's find a better way for handling ENV variables.
	private static String name = "HC_REASONS_WITH_STACKTRACE";

	public abstract GenericEntity deproxy();

	private static boolean appendStackTraceToReasons = resolveAppendStackTraceToReasons();

	public GmtsBaseEntityStub() {
		if (appendStackTraceToReasons && this instanceof Reason)
			((Reason) this).markStackTrace();
	}

	private static boolean resolveAppendStackTraceToReasons() {
		boolean jvmIsInDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
		if (jvmIsInDebug)
			return true;

		String env = System.getenv(name);
		if (env != null && env.toLowerCase().equals("true"))
			return true;

		return false;
	}

}
