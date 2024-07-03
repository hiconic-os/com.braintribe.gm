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
package com.braintribe.model.processing.core.commons;

import com.braintribe.cc.lcd.HashSupportWrapperCodec;
import com.braintribe.model.generic.commons.EntRefHashingComparator;
import com.braintribe.model.generic.value.EntityReference;

/**
 * Use {@link EntRefHashingComparator} directly with your CodingMap/Set.
 */
@Deprecated
public class EntityReferenceWrapperCodec extends HashSupportWrapperCodec<EntityReference> {
	
	public static final EntityReferenceWrapperCodec INSTANCE = new EntityReferenceWrapperCodec();
	
	public EntityReferenceWrapperCodec() {
		super(true);
	}
	
	@Override
	protected int entityHashCode(EntityReference e) {
		return EntRefHashingComparator.INSTANCE.computeHash(e);
	}

	@Override
	protected boolean entityEquals(EntityReference e1, EntityReference e2) {
		return EntRefHashingComparator.INSTANCE.compare(e1, e2);
	}			
}		
