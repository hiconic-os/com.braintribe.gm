// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.query.stringifier.experts;

import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.PreliminaryEntityReference;
import com.braintribe.model.processing.query.api.stringifier.QueryStringifierRuntimeException;
import com.braintribe.model.processing.query.api.stringifier.experts.Stringifier;
import com.braintribe.model.processing.query.stringifier.BasicQueryStringifierContext;

public class EntityReferenceStringifier<R extends EntityReference> implements Stringifier<R, BasicQueryStringifierContext> {
	@Override
	public String stringify(R reference, BasicQueryStringifierContext context) throws QueryStringifierRuntimeException {
		StringBuilder sb = new StringBuilder();

		sb.append("reference(");
		sb.append(context.getShortening().shorten(reference.getTypeSignature()));
		sb.append(", ");
		sb.append(context.stringify(reference.getRefId()));
		
		if (reference.getRefPartition() != null) {
			sb.append(", '");
			sb.append(reference.getRefPartition());
			sb.append("'");
		}

		if (reference instanceof PreliminaryEntityReference) {
			sb.append(", false");
		}

		sb.append(")");
		return sb.toString();
	}
}
