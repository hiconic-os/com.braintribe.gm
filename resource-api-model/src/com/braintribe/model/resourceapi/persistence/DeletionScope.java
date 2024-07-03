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
package com.braintribe.model.resourceapi.persistence;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.ResourceSource;

/**
 * A resource consists of a {@link Resource} entity, which itself has a {@link ResourceSource}, which again points to
 * the actual binary data of the resource. When you want to delete a resource, this enum lets you specify what part of
 * it you actually want to delete.
 * 
 * @author Neidhart.Orlich
 *
 */
public enum DeletionScope implements EnumBase {
	/**
	 * Delete the binary content of the resource but keep the {@link ResourceSource} and the {@link Resource} entity
	 */
	binary,
	/**
	 * Delete the binary content and the {@link ResourceSource} of the resource but keep the {@link Resource} entity
	 */
	source,
	/**
	 * Delete all: the binary content, the {@link ResourceSource} and the {@link Resource} entity itself.
	 */
	resource;

	public static final EnumType T = EnumTypes.T(DeletionScope.class);

	@Override
	public EnumType type() {
		return T;
	}
	
}