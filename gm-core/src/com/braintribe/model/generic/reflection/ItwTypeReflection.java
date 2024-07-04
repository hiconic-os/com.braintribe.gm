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

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.weaving.ProtoGmEntityType;

/**
 * @author peter.gazdik
 */
public interface ItwTypeReflection extends GenericModelTypeReflection {

	<T extends GenericModelType> T getDeployedType(String typeSignature);

	EnumType<?> deployEnumType(Class<? extends Enum<?>> enumClass);

	void deployEntityType(EntityType<?> entityType);

	/**
	 * Returns a ProtoGmEntityType created with "ProtoAnalysis" of corresponding {@link Class} object, or <tt>null</tt>, if no class is found via
	 * Class.forName(...)
	 * <p>
	 * This is used in ITW to make sure a type with given signature is always woven based on the class on classpath, if available, rather than using
	 * given {@link GmEntityType} instance, which (e.g. when coming from a remote system) might not correspond to the one on classpath.
	 */
	ProtoGmEntityType findProtoGmEntityType(String typeSignature);

}
