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
package com.braintribe.model.version;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * something that can deliver major and minor version parts
 * @author pit/dirk
 *
 */
@Abstract
public interface HasMajorMinor extends GenericEntity {
	
	EntityType<HasMajorMinor> T = EntityTypes.T(HasMajorMinor.class);
	static final String major = "major";
	static final String minor = "minor";

	/**
	 * @return - the major value
	 */
	int getMajor();
	void setMajor( int major);
	
	/**
	 * @return - the minor value
	 */
	Integer getMinor();
	void setMinor( Integer minor);
	
	default int minor() {
		Integer rv = getMinor();
		if (rv != null) {
			return rv;
		}
		return 0;
	}
}
