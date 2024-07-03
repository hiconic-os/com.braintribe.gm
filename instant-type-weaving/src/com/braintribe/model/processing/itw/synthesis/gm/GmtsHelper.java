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
package com.braintribe.model.processing.itw.synthesis.gm;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityInitializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.weaving.ProtoGmEntityType;

/**
 * @author peter.gazdik
 */
public class GmtsHelper {

	/* package */ static EntityInitializer[] toArrayOrNull(List<EntityInitializer> initializers) {
		int size = initializers.size();
		return size == 0 ? null : initializers.toArray(new EntityInitializer[size]);
	}

	/** Name for the enhanced class. */
	/* package */ static String getEnhancedClassName(ProtoGmEntityType gmEntityType) {
		return gmEntityType.getTypeSignature() + "-gm";
	}

	/** Name for the plain plain. */
	/* package */ static String getPlainClassName(ProtoGmEntityType gmEntityType) {
		return gmEntityType.getTypeSignature() + "-plain";
	}

	/** Name for the read/write interface which works with Objects (rather than actual property types) */
	/* package */ static String getWeakIfaceName(ProtoGmEntityType gmEntityType) {
		return gmEntityType.getTypeSignature() + "-weak";
	}

	/** Name for this entity's implementation of {@link EntityType}. */
	/* package */ static String getEntityTypeName(ProtoGmEntityType gmEntityType) {
		return gmEntityType.getTypeSignature() + "-et";
	}

	public static String getPropertyClassName(ProtoGmEntityType gmEntityType, String propertyName) {
		String s = gmEntityType.getTypeSignature();

		int pos = s.lastIndexOf('.');
		if (pos < 0)
			return propertyName + "--" + s;

		pos++;
		return s.substring(0, pos) + propertyName + "--" + s.substring(pos);
	}

}
