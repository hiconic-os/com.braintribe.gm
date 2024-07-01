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
package com.braintribe.model.resource.source;

import java.io.InputStream;

import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.annotation.Transient;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.resource.Resource;

/**
 * A {@link TransientSource} is created using an {@link InputStream}. Thus, there is no access to the underlying data itself. This is only a vehicle
 * for read access
 * 
 */

@ForwardDeclaration("com.braintribe.gm:transient-resource-model")
public interface TransientSource extends ResourceSource, StreamableSource {

	final EntityType<TransientSource> T = EntityTypes.T(TransientSource.class);

	/**
	 * The owning resource that has this transient source as ResourceSource.
	 */
	Resource getOwner();
	void setOwner(Resource owner);

	/**
	 * This must be re-streameble: Any time the {@link InputStreamProvider#openInputStream()} is called, a new {@link InputStream} for the exact same
	 * binary data must be returned.
	 */
	@Transient
	InputStreamProvider getInputStreamProvider();
	void setInputStreamProvider(InputStreamProvider inputStreamProvider);

	@Override
	default InputStreamProvider inputStreamProvider() {
		return getInputStreamProvider();
	}

	@Override
	default boolean hasTransientData() {
		return getInputStreamProvider() != null;
	}

}
