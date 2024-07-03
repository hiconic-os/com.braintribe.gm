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
package com.braintribe.model.meta.data.prompt;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.EntityTypeMetaData;

/**
* Metadata which configures if entities should be auto expanded in views which handle expansion.
* If it is present, then the view will show all available data.
* The depth may be a number or some known depth configuration (such as "shallow" or "reachable".
*
*/
public interface AutoExpand extends EntityTypeMetaData {
	
	EntityType<AutoExpand> T = EntityTypes.T(AutoExpand.class);
	
	String getDepth();
	void setDepth(String depth);

}