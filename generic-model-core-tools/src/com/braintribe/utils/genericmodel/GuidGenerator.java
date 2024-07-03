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
package com.braintribe.utils.genericmodel;

import java.util.function.Supplier;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.processing.IdGenerator;
import com.braintribe.utils.RandomTools;

/**
 * Returns a random 32 characters long string containing only hexadecimal characters,
 * {@link #setDtsPrefixEnabled(boolean) optionally} starting with a timestamp prefix. Note that the IDs generated are
 * similar to the GUIDs (usually) used in CSP. The algorithm is different though. <br>
 * This generator can e.g. used in tests, if <code>biz.i2z.service.ecm.access.impl.generator.GUIDGenerator</code> is not
 * available in the classpath.
 *
 * @author michael.lafite
 */
public class GuidGenerator implements IdGenerator, Supplier<String> {

	boolean dtsPrefixEnabled = true;

	public GuidGenerator() {
		// nothing to do
	}

	public boolean isDtsPrefixEnabled() {
		return this.dtsPrefixEnabled;
	}

	public void setDtsPrefixEnabled(final boolean dtsPrefixEnabled) {
		this.dtsPrefixEnabled = dtsPrefixEnabled;
	}

	@Override
	public Object generateId(final GenericEntity entity) throws Exception {
		return get();
	}

	@Override
	public String get() {
		return RandomTools.getRandom32CharactersHexString(isDtsPrefixEnabled());
	}

}
