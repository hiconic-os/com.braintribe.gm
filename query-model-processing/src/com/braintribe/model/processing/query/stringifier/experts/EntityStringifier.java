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
package com.braintribe.model.processing.query.stringifier.experts;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.api.stringifier.QueryStringifierRuntimeException;
import com.braintribe.model.processing.query.api.stringifier.experts.Stringifier;
import com.braintribe.model.processing.query.api.stringifier.experts.StringifierContext;

public class EntityStringifier implements Stringifier<GenericEntity,StringifierContext> {
	@Override
	public String stringify(GenericEntity entity, StringifierContext context) throws QueryStringifierRuntimeException {
		// TODO why are we creating a new instance of EntityReference just to write entity as String?
		// TODO Use something like following instead (or whatever the representation for an entity reference is):
		// TODO return entity.type().getTypeSignature() + "[" + entity.persistenceId() + ", " + entity.get$partition() + "]";

		return context.stringify(entity.reference());
	}
}
