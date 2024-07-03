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
package com.braintribe.model.processing.cmd.test.meta;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;

/**
 * Used to check if given meta data was expected to be active. If any instance returned in a test run returns false (via
 * the {@link #getActive()} method), then the test should fail (i.e. this flag describes, whether or not this MetaData
 * was expected to be returned).
 * <p>
 * The <tt>activeString</tt> makes it possible to better control what MD is expected. Use this if you want to set many
 * meta-data, but each resolution returns some other sub-set of them (e.g. due to different
 * {@link SelectorContextAspect})s.
 */
public interface ActivableMetaData extends MetaData {

	EntityType<ActivableMetaData> T = EntityTypes.T(ActivableMetaData.class);

	// @formatter:off
	boolean getActive();
	void setActive(boolean active);

	String getActiveString();
	void setActiveString(String s);
	// @formatter:on
}
