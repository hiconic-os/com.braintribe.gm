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
package com.braintribe.model.access.collaboration.distributed.api.model;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;

/**
 * Base type for {@link CsaOperation}s related to {@link Resource}s stored in the CSA.
 * 
 * @author peter.gazdik
 */
@Abstract
public interface CsaBinaryOperation extends CsaOperation {

	EntityType<CsaBinaryOperation> T = EntityTypes.T(CsaBinaryOperation.class);

	String resourceRelativePath = "resourceRelativePath";

	/** The relative path under which the actual binary data can be located, typically as a file in the file system. */
	String getResourceRelativePath();
	void setResourceRelativePath(String resourceRelativePath);

}
